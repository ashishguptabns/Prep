package LLD.DynamicMenu.model;

public class Ingredient {

    private final String name;
    private int quantity;

    public Ingredient(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public synchronized int getQuantity() {
        return quantity;
    }

    public synchronized void addQuantity(int qty) {
        this.quantity += qty;
    }

    public synchronized void deduceQuantity(int qty) {
        if (this.quantity >= qty) {
            this.quantity -= qty;
        }
    }
}
