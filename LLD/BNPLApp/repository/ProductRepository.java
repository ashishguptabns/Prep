package LLD.BNPLApp.repository;

import LLD.BNPLApp.entity.ProductEntity;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ProductRepository {
    private final Map<String, ProductEntity> products = new ConcurrentHashMap<>();

    public void save(ProductEntity product) {
        products.put(product.getProductId(), product);
    }

    public Optional<ProductEntity> findById(String productId) {
        return Optional.ofNullable(products.get(productId));
    }
}
