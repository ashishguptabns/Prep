package LLD.DynamicMenu.model;

import java.util.concurrent.locks.ReentrantLock;

public class Ingredient {

    private final String name;
    private int quantity;
    private final ReentrantLock lock = new ReentrantLock();

    public Ingredient(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void addQuantity(int qty) {
        this.quantity += qty;
    }

    public void deduceQuantity(int qty) {
        if (this.quantity >= qty) {
            this.quantity -= qty;
        }
    }
}
