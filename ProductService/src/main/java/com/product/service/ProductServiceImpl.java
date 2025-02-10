package com.product.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.product.model.Category;
import com.product.model.Price;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.product.model.Product;
import com.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repository;


    public Product saveProduct(Product product) {
        return repository.save(product);
    }

    public List<Product> saveProducts(List<Product> products) {
        return repository.saveAll(products);
    }

    public List<Product> getProducts() {
        return repository.findAll();
    }

    public Optional<Product> getProductById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Product> getProductByName(String name) {
        return Optional.ofNullable(repository.findByName(name));
    }

    public String deleteProduct(UUID id) {
        repository.deleteById(id);
        return "product removed !! " + id;
    }


    public Product createProduct(String name, String description, BigDecimal price, UUID categoryId) {
        Product product = new Product(name, description, new Price(price), new Category(categoryId));
        return repository.save(product);
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    @Override
    public void updateProductPrice(UUID id, BigDecimal newPrice) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.updatePrice(newPrice);
        repository.save(product);
    }

    @Override

    @Transactional
    public Product updateProductByName(Product updatedProduct) {
        Optional<Product> existingProductOpt = Optional.ofNullable(repository.findByName(updatedProduct.getName()));
        if (existingProductOpt.isEmpty()) {
            throw new IllegalArgumentException("Product with name " + updatedProduct.getName() + " not found.");
        }
        Product existingProduct = existingProductOpt.get();
        if (updatedProduct.getPrice() != null) {
            existingProduct.setPrice(updatedProduct.getPrice());
        }

        if (updatedProduct.getCategory() != null) {
            existingProduct.setCategory(updatedProduct.getCategory());
        }

        return repository.save(existingProduct);
    }
}
