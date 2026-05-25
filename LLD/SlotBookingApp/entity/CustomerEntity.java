package LLD.SlotBookingApp.entity;

import java.util.UUID;

public class CustomerEntity {
    private final String customerId;
    private final String name;

    public CustomerEntity(String name) {
        this.customerId = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }
}
