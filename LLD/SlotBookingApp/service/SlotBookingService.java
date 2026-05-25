package LLD.SlotBookingApp.service;

import LLD.SlotBookingApp.entity.BookingEntity;
import LLD.SlotBookingApp.entity.CenterEntity;
import LLD.SlotBookingApp.entity.CustomerEntity;
import LLD.SlotBookingApp.entity.SlotEntity;
import LLD.SlotBookingApp.model.BookingStatus;
import LLD.SlotBookingApp.model.SlotView;
import LLD.SlotBookingApp.model.WorkoutType;
import LLD.SlotBookingApp.repository.BookingRepository;
import LLD.SlotBookingApp.repository.CenterRepository;
import LLD.SlotBookingApp.repository.SlotRepository;
import LLD.SlotBookingApp.repository.WaitlistRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SlotBookingService {
    private final CenterRepository centerRepository;
    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final WaitlistRepository waitlistRepository;
    private final Map<String, Object> slotLocks = new ConcurrentHashMap<>();

    public SlotBookingService(CenterRepository centerRepository, SlotRepository slotRepository,
            BookingRepository bookingRepository, WaitlistRepository waitlistRepository) {
        this.centerRepository = centerRepository;
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
        this.waitlistRepository = waitlistRepository;
    }

    public CenterEntity createCenter(String name, LocalTime opensAt, LocalTime closesAt) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Center name is required");
        }
        if (!opensAt.isBefore(closesAt)) {
            throw new IllegalArgumentException("Opening time must be before closing time");
        }
        CenterEntity center = new CenterEntity(name, opensAt, closesAt);
        centerRepository.save(center);
        return center;
    }

    public SlotEntity createSlot(String centerId, LocalDate date, LocalTime startsAt, LocalTime endsAt,
            WorkoutType workoutType, int capacity) {
        CenterEntity center = centerRepository.findById(centerId)
                .orElseThrow(() -> new IllegalArgumentException("Center not found: " + centerId));
        validateSlot(center, startsAt, endsAt, capacity);

        SlotEntity slot = new SlotEntity(centerId, date, startsAt, endsAt, workoutType, capacity);
        slotRepository.save(slot);
        return slot;
    }

    public BookingEntity bookSlot(CustomerEntity customer, String slotId) {
        SlotEntity slot = findSlot(slotId);
        synchronized (lockFor(slotId)) {
            bookingRepository.findActiveByCustomerAndSlot(customer.getCustomerId(), slotId)
                    .ifPresent(existing -> {
                        throw new IllegalStateException("Customer already has an active booking for this slot");
                    });

            BookingStatus status = confirmedCount(slotId) < slot.getCapacity()
                    ? BookingStatus.CONFIRMED
                    : BookingStatus.WAITLISTED;
            BookingEntity booking = new BookingEntity(customer.getCustomerId(), slotId, status);
            bookingRepository.save(booking);
            if (status == BookingStatus.WAITLISTED) {
                waitlistRepository.add(slotId, booking.getBookingId());
            }
            return booking;
        }
    }

    public void cancelBooking(String bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        synchronized (lockFor(booking.getSlotId())) {
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                return;
            }
            if (booking.getStatus() == BookingStatus.WAITLISTED) {
                waitlistRepository.remove(booking.getSlotId(), booking.getBookingId());
                booking.setStatus(BookingStatus.CANCELLED);
                return;
            }

            booking.setStatus(BookingStatus.CANCELLED);
            promoteNextWaitlistedBooking(booking.getSlotId());
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
            throw new IllegalArgumentException("Slot capacity must be positive");
        }
        if (!startsAt.isBefore(endsAt)) {
            throw new IllegalArgumentException("Slot start time must be before end time");
        }
        if (startsAt.isBefore(center.getOpensAt()) || endsAt.isAfter(center.getClosesAt())) {
            throw new IllegalArgumentException("Slot must be inside center operating hours");
        }
    }

    private SlotEntity findSlot(String slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found: " + slotId));
    }

    private int confirmedCount(String slotId) {
        return bookingRepository.findBySlotAndStatus(slotId, BookingStatus.CONFIRMED).size();
    }

    private void promoteNextWaitlistedBooking(String slotId) {
        Optional<String> nextBookingId = waitlistRepository.poll(slotId);
        nextBookingId.flatMap(bookingRepository::findById)
                .filter(booking -> booking.getStatus() == BookingStatus.WAITLISTED)
                .ifPresent(booking -> booking.setStatus(BookingStatus.CONFIRMED));
    }

    private SlotView toView(SlotEntity slot) {
        return new SlotView(slot, confirmedCount(slot.getSlotId()), waitlistRepository.count(slot.getSlotId()));
    }

    private Object lockFor(String slotId) {
        return slotLocks.computeIfAbsent(slotId, id -> new Object());
    }
}
