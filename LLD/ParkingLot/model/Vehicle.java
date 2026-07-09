package LLD.ParkingLot.model;

import java.util.UUID;

public class Vehicle {

    private final String vehicleId;
    private final String licensePlate;

    public Vehicle(String licensePlate) {
        this.vehicleId = UUID.randomUUID().toString();
        this.licensePlate = licensePlate;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    @Override
    public String toString() {
        return "Vehicle{vehicleId='" + vehicleId + "', licensePlate='" + licensePlate + "'}";
    }
}
