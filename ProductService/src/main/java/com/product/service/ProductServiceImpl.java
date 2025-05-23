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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.product.model.Product;
import com.product.repository.ProductRepository;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ConcurrentMap<UUID, ReentrantLock> productLocks = new ConcurrentHashMap<>();
    @Autowired
    public ProductServiceImpl(ProductRepository repository,
                              ApplicationEventPublisher eventPublisher,
                              KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.kafkaTemplate = kafkaTemplate;
    }

    private ReentrantLock getLock(UUID productId) {
        return productLocks.computeIfAbsent(productId, id -> new ReentrantLock());
    }

    @Override
    @Cacheable(value = "products", key = "'all'")
    public List<Product> getProducts() {
        log.debug("Fetching all products from DB");
        return repository.findAll();
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public Optional<Product> getProductById(UUID id) {
        log.debug("Fetching product by ID: {}", id);
        return repository.findById(id);
    }

    @Override
    @Cacheable(value = "products", key = "#name")
    public Optional<Product> getProductByName(String name) {
        log.debug("Fetching product by Name: {}", name);
        return Optional.ofNullable(repository.findByName(name));
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product saveProduct(Product product) {
        log.info("Saving new product: {}", product.getName());
        Product saved = repository.save(product);
        publishProductEvent(saved, "product.created.topic");
        return saved;
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public List<Product> saveProducts(List<Product> products) {
        log.info("Saving batch of products");
        List<Product> saved = repository.saveAll(products);
        saved.forEach(p -> publishProductEvent(p, "product.created.topic"));
        return saved;
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public String deleteProduct(UUID id) {
        log.warn("Deleting product: {}", id);
        ReentrantLock lock = getLock(id);
        lock.lock();
        try {
            repository.deleteById(id);
            publishDeletionEvent(id);
            return "Product removed !! " + id;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(String name, String description, BigDecimal price, UUID categoryId) {
        Product product = new Product(name, description, new Price(price), new Category(categoryId));
        log.info("Creating product: {}", name);
        Product saved = repository.save(product);
        publishProductEvent(saved, "product.created.topic");
        return saved;
    }

    @Override
    public List<Product> getAllProducts() {
        return getProducts();
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public void updateProductPrice(UUID id, BigDecimal newPrice) {
        log.info("Updating price for product {} to {}", id, newPrice);
        ReentrantLock lock = getLock(id);
        lock.lock();
        try {
            Product product = repository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            product.updatePrice(newPrice);
            Product updated = repository.save(product);
            publishProductEvent(updated, "product.updated.topic");
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProductByName(Product updatedProduct) {
        String name = updatedProduct.getName();
        log.info("Updating product by name: {}", name);
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

            Product updated = repository.save(existingProduct);
            publishProductEvent(updated, "product.updated.topic");
            return updated;
        } finally {
            lock.unlock();
        }
    }

    private void publishProductEvent(Product product, String topic) {
        kafkaTemplate.send(topic, product);
        eventPublisher.publishEvent(product);
        log.debug("Published event to topic {}: {}", topic, product);
    }

    private void publishDeletionEvent(UUID productId) {
        kafkaTemplate.send("product.deleted.topic", productId);
        eventPublisher.publishEvent(productId);
        log.debug("Published product deletion event: {}", productId);
    }
}
