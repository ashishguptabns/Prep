package LLD;

import java.util.LinkedList;
import java.util.List;

enum VehicleType {
    CAR, BIKE, TRUCK
}

class Vehicle {
    String id;
    VehicleType type;

    public VehicleType getType() {
        return this.type;
    }

}

class Car extends Vehicle {
    public Car(String id) {
        this.id = id;
        this.type = VehicleType.CAR;
    }
}

class Bike extends Vehicle {
    public Bike(String id) {
        this.id = id;
        this.type = VehicleType.BIKE;
    }
}

class Truck extends Vehicle {
    public Truck(String id) {
        this.id = id;
        this.type = VehicleType.TRUCK;
    }
}

class Floor {
    final Spot[] spots;
    public final int num;

    public Floor(int num, int spotSize) {
        this.spots = new Spot[spotSize];
        for (int i = 0; i < spotSize; i++) {
            this.spots[i] = new Spot(i, null);
        }
        this.num = num;
    }

    public void parkSpot(Spot spot) {
        this.spots[spot.num] = spot;
    }

    public boolean hasEmptySpots() {
        return true;
    }

    public boolean unParkSpot(Ticket ticket) {
        if (this.spots[ticket.spot.num].vehicle != null
                && this.spots[ticket.spot.num].vehicle
                        .equals(ticket.spot.vehicle)) {
            ticket.spot.isTaken = false;
            ticket.spot.vehicle = null;
            return true;
        }
        return false;
    }

    public Spot findSpot() {
        for (Spot spot : this.spots) {
            if (!spot.isTaken) {
                return spot;
            }
        }
        return null;
    }
}

class Spot {

    public boolean isTaken;
    Vehicle vehicle;
    public final int num;

    public Spot(int num, Vehicle vehicle) {
        this.vehicle = vehicle;
        this.num = num;
    }
}

class Ticket {

    final Vehicle vehicle;
    final Spot spot;
    final Floor floor;
    final String entryTime;

    public Ticket(Floor floor, Spot spot, Vehicle vehicle) {
        this.vehicle = vehicle;
        this.spot = spot;
        this.floor = floor;
        this.entryTime = String.valueOf(System.currentTimeMillis());
        this.spot.isTaken = true;
    }

    public String getEntryTime() {
        return entryTime;
    }

}

public class ParkingLotApp {

    Floor[] floors;
    int numSpotsTaken = 0;

    public ParkingLotApp(int floorSize, int spotSize) {
        this.floors = new Floor[floorSize];
        for (int i = 0; i < floors.length; i++) {
            floors[i] = new Floor(i, spotSize);
        }
    }

    public static void main(String[] args) {
        testVehicleTypes();
        testParkingAndUnparking();
        testAvailabilityTracking();
        testMultiFloorSupport();
        testTicketingSystem();
        testThreadSafety();
        System.out.println("All tests ran (implement logic to pass them).\n");
    }

    static void testVehicleTypes() {
        Vehicle car = new Car("KA-01-1234");
        Vehicle bike = new Bike("KA-02-5678");
        Vehicle truck = new Truck("KA-03-9999");
        assert car.getType() == VehicleType.CAR : "Car type failed";
        assert bike.getType() == VehicleType.BIKE : "Bike type failed";
        assert truck.getType() == VehicleType.TRUCK : "Truck type failed";
    }

    static void testParkingAndUnparking() {
        ParkingLotApp lot = new ParkingLotApp(1, 2); // 1 floor, 2 spots
        Vehicle car1 = new Car("C1");
        Vehicle car2 = new Car("C2");
        Vehicle car3 = new Car("C3");
        Ticket t1 = lot.park(car1);
        Ticket t2 = lot.park(car2);
        assert t1 != null : "First car should park";
        assert t2 != null : "Second car should park";
        // Check correct spot assignment
        assert t1.spot != t2.spot : "Cars should not get same spot";
        assert t1.spot.isTaken && t2.spot.isTaken : "Spots should be marked taken";
        // Try to park when full
        Ticket t3 = lot.park(car3);
        assert t3 == null : "Should not park when full";
        // Unpark and check spot is free and vehicle removed
        boolean unparked = lot.unpark(t1);
        assert unparked : "Unparking failed";
        assert !t1.spot.isTaken : "Spot should be free after unpark";
        assert t1.spot.vehicle == null : "Vehicle should be removed from spot after unpark";
        // Park again after freeing a spot
        Ticket t4 = lot.park(car3);
        assert t4 != null : "Should park after spot is freed";
        assert t4.spot == t1.spot : "Should reuse freed spot";
        // Double parking same car should not be allowed (if logic is added)
        Ticket t5 = lot.park(car3);
        assert t5 == null : "Double parking same car should not be allowed";
        // Unpark with invalid ticket
        boolean invalidUnpark = lot.unpark(new Ticket(lot.floors[0], lot.floors[0].spots[0], new Car("fake")));
        assert !invalidUnpark : "Unparking with invalid ticket should fail";
    }

    private boolean unpark(Ticket ticket) {
        boolean isValid = this.floors[ticket.floor.num].unParkSpot(ticket);
        if (!isValid) {
            return false;
        }
        this.numSpotsTaken--;
        return true;
    }

    private Ticket park(Vehicle car) {
        if (isSpotAvailable()) {
            Floor floor = findFloor();
            Spot spot = bookSpot(floor, car);
            this.numSpotsTaken++;
            return new Ticket(floor, spot, car);
        }
        return null;
    }

    private boolean isSpotAvailable() {
        return numSpotsTaken < this.floors.length * this.floors[0].spots.length;
    }

    private Spot bookSpot(Floor floor, Vehicle vehicle) {
        Spot spot = floor.findSpot();
        floor.parkSpot(spot);
        return spot;
    }

    private Floor findFloor() {
        if (this.floors[this.floors.length - 1].hasEmptySpots()) {
            return this.floors[this.floors.length - 1];
        } else {
            return this.floors[this.floors.length];
        }
    }

    static void testMultiFloorSupport() {
        ParkingLotApp lot = new ParkingLotApp(3, 5);
        assert lot.getFloors().size() == 3 : "Multi-floor support failed";
    }

    static void testAvailabilityTracking() {
        ParkingLotApp lot = new ParkingLotApp(1, 2);
        Vehicle car1 = new Car("A");
        Vehicle car2 = new Car("B");
        assert lot.getAvailableSpots(VehicleType.CAR) == 2 : "Should start with 2 available spots";
        lot.park(car1);
        assert lot.getAvailableSpots(VehicleType.CAR) == 1 : "Should have 1 available after 1 park";
        lot.park(car2);
        assert lot.getAvailableSpots(VehicleType.CAR) == 0 : "Should have 0 available after 2 parks";
        // Unpark and check
        Ticket t = lot.park(new Car("C"));
        assert t == null : "Should not park when full";
        lot.unpark(new Ticket(lot.floors[0], lot.floors[0].spots[0], car1));
        assert lot.getAvailableSpots(VehicleType.CAR) == 1 : "Should have 1 available after unpark";
        // Try to unpark again (should fail)
        boolean again = lot.unpark(new Ticket(lot.floors[0], lot.floors[0].spots[0], car1));
        assert !again : "Unparking already free spot should fail";
    }

    public List<Floor> getFloors() {
        return List.of(this.floors);
    }

    public int getAvailableSpots(VehicleType type) {
        int available = 0;
        for (Floor floor : floors) {
            for (Spot spot : floor.spots) {
                if (!spot.isTaken)
                    available++;
            }
        }
        return available;
    }

    static void testTicketingSystem() {
        ParkingLotApp lot = new ParkingLotApp(1, 1);
        Vehicle car = new Car("KA-01-1234");
        Ticket ticket = lot.park(car);
        assert ticket.getEntryTime() != null : "Ticketing system failed";
    }

    static void testThreadSafety() {
        final ParkingLotApp lot = new ParkingLotApp(1, 10);
        final int threadCount = 20;
        Thread[] threads = new Thread[threadCount];
        final int[] parked = { 0 };
        final List<Ticket> tickets = new LinkedList<>();
        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            threads[i] = new Thread(() -> {
                Vehicle car = new Car("T-" + idx);
                Ticket ticket = lot.park(car);
                if (ticket != null) {
                    synchronized (parked) {
                        parked[0]++;
                        tickets.add(ticket);
                    }
                }
            });
        }
        try {
            for (Thread t : threads)
                t.start();
            for (Thread t : threads)
                t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Only 10 should be parked, no more
        assert parked[0] == 10 : "Thread safety test failed: " + parked[0];
        // Now unpark all and check all spots are free
        for (Ticket ticket : tickets) {
            boolean ok = lot.unpark(ticket);
            assert ok : "Unparking after thread test failed";
        }
        assert lot.getAvailableSpots(VehicleType.CAR) == 10 : "All spots should be free after thread unpark";
    }
}
