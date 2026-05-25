package LLD.FlashSaleApp.repository;

import java.util.Optional;

import LLD.FlashSaleApp.entity.ProductEntity;

public interface ProductStore {
    void save(ProductEntity product);

    Optional<ProductEntity> findById(String productId);
}
