package LLD.SlotBookingApp;

import LLD.SlotBookingApp.entity.BookingEntity;
import LLD.SlotBookingApp.entity.CenterEntity;
import LLD.SlotBookingApp.entity.CustomerEntity;
import LLD.SlotBookingApp.entity.SlotEntity;
import LLD.SlotBookingApp.model.BookingStatus;
import LLD.SlotBookingApp.model.WorkoutType;
import LLD.SlotBookingApp.repository.BookingRepository;
import LLD.SlotBookingApp.repository.CenterRepository;
import LLD.SlotBookingApp.repository.SlotRepository;
import LLD.SlotBookingApp.repository.WaitlistRepository;
import LLD.SlotBookingApp.service.SlotBookingService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Driver {
    public static void main(String[] args) throws InterruptedException {
        SlotBookingService service = new SlotBookingService(
                new CenterRepository(),
                new SlotRepository(),
                new BookingRepository(),
                new WaitlistRepository());

        CenterEntity center = service.createCenter("Koramangala Fit Club",
                LocalTime.of(6, 0), LocalTime.of(22, 0));
        SlotEntity slot = service.createSlot(center.getCenterId(), LocalDate.now(),
                LocalTime.of(7, 0), LocalTime.of(8, 0), WorkoutType.YOGA, 2);

        List<CustomerEntity> customers = List.of(
                new CustomerEntity("Asha"),
                new CustomerEntity("Dev"),
                new CustomerEntity("Mira"),
                new CustomerEntity("Kabir"));
        List<BookingEntity> bookings = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(customers.size());
        for (CustomerEntity customer : customers) {
            executor.submit(() -> {
                try {
                    BookingEntity booking = service.bookSlot(customer, slot.getSlotId());
                    bookings.add(booking);
                    System.out.println(customer.getName() + " -> " + booking.getStatus());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        BookingEntity confirmedBooking = bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .findFirst()
                .orElseThrow();
        service.cancelBooking(confirmedBooking.getBookingId());

        System.out.println("Cancelled booking: " + confirmedBooking.getBookingId());
        System.out.println(service.getSlotView(slot.getSlotId()));
        for (BookingEntity booking : bookings) {
            System.out.println(booking);
        }
    }
}
