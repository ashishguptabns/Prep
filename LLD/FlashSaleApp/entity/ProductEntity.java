package LLD.FlashSaleApp.entity;

import java.util.UUID;

public class ProductEntity {
    private final String productId;
    private final String name;
    private final long price;

    public ProductEntity(String name, long price) {
        this.productId = UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "ProductEntity{productId='" + productId + "', name='" + name
                + "', price=" + price + "}";
    }
}
