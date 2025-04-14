package com.product.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import com.product.model.Category;
import com.product.model.Price;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.product.model.Product;
import com.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    // Per-product lock map for thread-safe updates
    private final ConcurrentMap<UUID, ReentrantLock> productLocks = new ConcurrentHashMap<>();

    @Autowired
    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    private ReentrantLock getLock(UUID productId) {
        return productLocks.computeIfAbsent(productId, id -> new ReentrantLock());
    }

    @Override
    @Cacheable("products")
    public List<Product> getProducts() {
        return repository.findAll();
    }

    @Override
    @Cacheable("products")
    public Optional<Product> getProductById(UUID id) {
        return repository.findById(id);
    }

    @Override
    @Cacheable("products")
    public Optional<Product> getProductByName(String name) {
        return Optional.ofNullable(repository.findByName(name));
    }

    @Override
    public Product saveProduct(Product product) {
        return repository.save(product);
    }

    @Override
    public List<Product> saveProducts(List<Product> products) {
        return repository.saveAll(products);
    }

    @Override
    public String deleteProduct(UUID id) {
        ReentrantLock lock = getLock(id);
        lock.lock();
        try {
            repository.deleteById(id);
            return "Product removed !! " + id;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Product createProduct(String name, String description, BigDecimal price, UUID categoryId) {
        Product product = new Product(name, description, new Price(price), new Category(categoryId));
        return repository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    @Override
    public void updateProductPrice(UUID id, BigDecimal newPrice) {
        ReentrantLock lock = getLock(id);
        lock.lock();
        try {
            Product product = repository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            product.updatePrice(newPrice);
            repository.save(product);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public Product updateProductByName(Product updatedProduct) {
        String name = updatedProduct.getName();
        Product existingProduct = repository.findByName(name);
        if (existingProduct == null) {
            throw new IllegalArgumentException("Product with name " + name + " not found.");
        }
        UUID productId = existingProduct.getId();
        ReentrantLock lock = getLock(productId);
        lock.lock();
        try {
            if (updatedProduct.getPrice() != null) {
                existingProduct.setPrice(updatedProduct.getPrice());
            }
            if (updatedProduct.getCategory() != null) {
                existingProduct.setCategory(updatedProduct.getCategory());
            }
            return repository.save(existingProduct);
        } finally {
            lock.unlock();
        }
    }
}