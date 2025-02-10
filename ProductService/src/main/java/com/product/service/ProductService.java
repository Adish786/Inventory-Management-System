package com.product.service;

import com.product.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {
    Product saveProduct(Product product);

    List<Product> saveProducts(List<Product> products);

    List<Product> getProducts();

    Optional<Product> getProductById(UUID id);

  Optional<  Product> getProductByName(String name);

    String deleteProduct(UUID id);

    Product createProduct(String name, String description, BigDecimal price, UUID categoryId);

    List<Product> getAllProducts();

    void updateProductPrice(UUID id, BigDecimal newPrice);

    Product updateProductByName(Product product);

}
