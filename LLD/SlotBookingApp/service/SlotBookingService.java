package LLD.SlotBookingApp.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import LLD.SlotBookingApp.entity.BookingEntity;
import LLD.SlotBookingApp.entity.CenterEntity;
import LLD.SlotBookingApp.entity.CustomerEntity;
import LLD.SlotBookingApp.entity.SlotEntity;
import LLD.SlotBookingApp.exception.SlotBookingException;
import LLD.SlotBookingApp.inventory.InMemorySlotInventory;
import LLD.SlotBookingApp.inventory.SlotInventory;
import LLD.SlotBookingApp.model.BookingStatus;
import LLD.SlotBookingApp.model.SlotView;
import LLD.SlotBookingApp.model.WorkoutType;
import LLD.SlotBookingApp.repository.ActiveBookingRegistry;
import LLD.SlotBookingApp.repository.BookingRepository;
import LLD.SlotBookingApp.repository.CenterRepository;
import LLD.SlotBookingApp.repository.InMemoryActiveBookingRegistry;
import LLD.SlotBookingApp.repository.SlotRepository;
import LLD.SlotBookingApp.repository.WaitlistRepository;
import LLD.SlotBookingApp.saga.BookingSaga;
import LLD.SlotBookingApp.strategy.FifoWaitlistPromotionStrategy;
import LLD.SlotBookingApp.strategy.WaitlistPromotionStrategy;

public class SlotBookingService {

    private final CenterRepository centerRepository;
    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final ActiveBookingRegistry activeBookingRegistry;
    private final WaitlistRepository waitlistRepository;
    private final WaitlistPromotionStrategy waitlistPromotionStrategy;
    private final SlotInventory slotInventory;

    public SlotBookingService(CenterRepository centerRepository, SlotRepository slotRepository,
            BookingRepository bookingRepository, WaitlistRepository waitlistRepository) {
        this(centerRepository, slotRepository, bookingRepository, waitlistRepository,
                new FifoWaitlistPromotionStrategy(waitlistRepository),
                new InMemoryActiveBookingRegistry(),
                new InMemorySlotInventory());
    }

    public SlotBookingService(CenterRepository centerRepository, SlotRepository slotRepository,
            BookingRepository bookingRepository, WaitlistRepository waitlistRepository,
            WaitlistPromotionStrategy waitlistPromotionStrategy) {
        this(centerRepository, slotRepository, bookingRepository, waitlistRepository,
                waitlistPromotionStrategy,
                new InMemoryActiveBookingRegistry(),
                new InMemorySlotInventory());
    }

    public SlotBookingService(CenterRepository centerRepository, SlotRepository slotRepository,
            BookingRepository bookingRepository, WaitlistRepository waitlistRepository,
            WaitlistPromotionStrategy waitlistPromotionStrategy,
            ActiveBookingRegistry activeBookingRegistry,
            SlotInventory slotInventory) {
        this.centerRepository = centerRepository;
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
        this.waitlistRepository = waitlistRepository;
        this.waitlistPromotionStrategy = waitlistPromotionStrategy;
        this.activeBookingRegistry = activeBookingRegistry;
        this.slotInventory = slotInventory;
    }

    public CenterEntity createCenter(String name, LocalTime opensAt, LocalTime closesAt) {
        if (name == null || name.isBlank()) {
            throw new SlotBookingException("Center name is required");
        }
        if (!opensAt.isBefore(closesAt)) {
            throw new SlotBookingException("Opening time must be before closing time");
        }
        CenterEntity center = new CenterEntity(name, opensAt, closesAt);
        centerRepository.save(center);
        return center;
    }

    public SlotEntity createSlot(String centerId, LocalDate date, LocalTime startsAt, LocalTime endsAt,
            WorkoutType workoutType, int capacity) {
        CenterEntity center = centerRepository.findById(centerId)
                .orElseThrow(() -> new SlotBookingException("Center not found: " + centerId));
        validateSlot(center, startsAt, endsAt, capacity);

        SlotEntity slot = new SlotEntity(centerId, date, startsAt, endsAt, workoutType, capacity);
        slotRepository.save(slot);
        slotInventory.registerSlot(slot.getSlotId());
        return slot;
    }

    public BookingEntity bookSlot(CustomerEntity customer, String slotId) {
        SlotEntity slot = findSlot(slotId);
        BookingEntity booking = new BookingEntity(customer.getCustomerId(), slotId, BookingStatus.WAITLISTED);
        BookingSaga saga = new BookingSaga();
        try {
            if (!activeBookingRegistry.reserve(customer.getCustomerId(), slotId, booking.getBookingId())) {
                throw new SlotBookingException("Customer already has an active booking for this slot");
            }
            saga.addCompensation(() -> activeBookingRegistry.release(
                    customer.getCustomerId(), slotId, booking.getBookingId()));

            if (slotInventory.tryReserve(slotId, slot.getCapacity())) {
                booking.setStatus(BookingStatus.CONFIRMED);
                saga.addCompensation(() -> slotInventory.release(slotId));
            }

            bookingRepository.save(booking);
            saga.addCompensation(() -> bookingRepository.delete(booking.getBookingId()));

            if (booking.getStatus() == BookingStatus.WAITLISTED) {
                waitlistRepository.add(slotId, booking.getBookingId());
                saga.addCompensation(() -> waitlistRepository.remove(slotId, booking.getBookingId()));
            }

            saga.complete();
            return booking;
        } catch (RuntimeException exception) {
            saga.compensate();
            throw exception;
        }
    }

    public void cancelBooking(String bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new SlotBookingException("Booking not found: " + bookingId));

        while (true) {
            BookingStatus status = booking.getStatus();
            if (status == BookingStatus.CANCELLED) {
                return;
            }

            if (status == BookingStatus.WAITLISTED) {
                if (booking.compareAndSetStatus(BookingStatus.WAITLISTED, BookingStatus.CANCELLED)) {
                    waitlistRepository.remove(booking.getSlotId(), booking.getBookingId());
                    releaseActiveBooking(booking);
                    return;
                }
                continue;
            }

            if (booking.compareAndSetStatus(BookingStatus.CONFIRMED, BookingStatus.CANCELLED)) {
                releaseActiveBooking(booking);
                if (!promoteNextWaitlistedBooking(booking.getSlotId())) {
                    slotInventory.release(booking.getSlotId());
                }
                return;
            }
        }
    }

    public List<SlotView> findSlots(String centerId, WorkoutType workoutType) {
        List<SlotView> result = new ArrayList<>();
        for (SlotEntity slot : slotRepository.findByCenterAndWorkout(centerId, workoutType)) {
            result.add(toView(slot));
        }
        return result;
    }

    public SlotView getSlotView(String slotId) {
        return toView(findSlot(slotId));
    }

    private void validateSlot(CenterEntity center, LocalTime startsAt, LocalTime endsAt, int capacity) {
        if (capacity <= 0) {
            throw new SlotBookingException("Slot capacity must be positive");
        }
        if (!startsAt.isBefore(endsAt)) {
            throw new SlotBookingException("Slot start time must be before end time");
        }
        if (startsAt.isBefore(center.getOpensAt()) || endsAt.isAfter(center.getClosesAt())) {
            throw new SlotBookingException("Slot must be inside center operating hours");
        }
    }

    private SlotEntity findSlot(String slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotBookingException("Slot not found: " + slotId));
    }

    private int confirmedCount(String slotId) {
        return slotInventory.confirmedCount(slotId);
    }

    private boolean promoteNextWaitlistedBooking(String slotId) {
        Optional<String> nextBookingId = waitlistPromotionStrategy.nextBookingId(slotId);
        while (nextBookingId.isPresent()) {
            Optional<BookingEntity> nextBooking = nextBookingId.flatMap(bookingRepository::findById);
            if (nextBooking.isPresent()
                    && nextBooking.get().compareAndSetStatus(BookingStatus.WAITLISTED, BookingStatus.CONFIRMED)) {
                return true;
            }
            nextBookingId = waitlistPromotionStrategy.nextBookingId(slotId);
        }
        return false;
    }

    private void releaseActiveBooking(BookingEntity booking) {
        activeBookingRegistry.release(booking.getCustomerId(), booking.getSlotId(), booking.getBookingId());
    }

    private SlotView toView(SlotEntity slot) {
        return new SlotView(slot, confirmedCount(slot.getSlotId()), waitlistRepository.count(slot.getSlotId()));
    }
}
