package LLD.FlashSaleApp.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import LLD.FlashSaleApp.entity.ProductEntity;

public class ProductRepository implements ProductStore {
    private final Map<String, ProductEntity> products = new ConcurrentHashMap<>();

    @Override
    public void save(ProductEntity product) {
        products.put(product.getProductId(), product);
    }

    @Override
    public Optional<ProductEntity> findById(String productId) {
        return Optional.ofNullable(products.get(productId));
    }
}
