package com.inventory.service;

import com.inventory.event.StockUpdatedEvent;
import com.inventory.model.Inventory;
import com.inventory.model.StockQuantity;
import com.inventory.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final RedisTemplate<String, Integer> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ConcurrentHashMap<UUID, ReentrantLock> productLocks = new ConcurrentHashMap<>();

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                RedisTemplate<String, Integer> redisTemplate,
                                ApplicationEventPublisher eventPublisher,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.redisTemplate = redisTemplate;
        this.eventPublisher = eventPublisher;
        this.kafkaTemplate = kafkaTemplate;
    }

    private ReentrantLock getLock(UUID productId) {
        return productLocks.computeIfAbsent(productId, id -> new ReentrantLock());
    }

    @Override
    @Transactional
    @Cacheable(value = "product", key = "#productId")
    public int getStock(UUID productId) {
        String cacheKey = "stock_" + productId;
        Integer cachedStock = redisTemplate.opsForValue().get(cacheKey);

        if (cachedStock != null) {
            log.debug("Cache hit for productId {} with stock {}", productId, cachedStock);
            return cachedStock;
        }

        Inventory inventory = inventoryRepository.findByProductId(productId).orElse(null);
        if (inventory != null) {
            int stock = inventory.getStockQuantity().getQuantity();
            redisTemplate.opsForValue().set(cacheKey, stock);
            log.info("Cache miss for productId {}. Fetched from DB with stock {}", productId, stock);
            return stock;
        }

        log.warn("Stock not found for productId {}", productId);
        return 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#productId")
    public void updateStock(UUID productId, StockQuantity quantity) {
        ReentrantLock lock = getLock(productId);
        lock.lock();
        try {
            Inventory inventory = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found: " + productId));

            inventory.setStockQuantity(quantity);
            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);

            redisTemplate.opsForValue().set("stock_" + productId, quantity.getQuantity());
            log.info("Stock set for productId {} to {}", productId, quantity.getQuantity());

            publishStockUpdate(productId, quantity.getQuantity());

        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#productId")
    public void updateStock(UUID productId, int quantity, boolean increase) {
        ReentrantLock lock = getLock(productId);
        lock.lock();
        try {
            Inventory inventory = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found: " + productId));

            if (increase) {
                inventory.increaseStock(quantity);
            } else {
                inventory.decreaseStock(quantity);
            }

            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);

            int newQuantity = inventory.getStockQuantity().getQuantity();
            redisTemplate.opsForValue().set("stock_" + productId, newQuantity);

            log.info("Stock {} for productId {} by {}. New stock: {}",
                    (increase ? "increased" : "decreased"), productId, quantity, newQuantity);

            publishStockUpdate(productId, newQuantity);

        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#productId")
    public void increaseStock(UUID productId, int quantity) {
        updateStock(productId, quantity, true);
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#productId")
    public void decreaseStock(UUID productId, int quantity) {
        updateStock(productId, quantity, false);
    }

    private void publishStockUpdate(UUID productId, int quantity) {
        StockUpdatedEvent event = new StockUpdatedEvent(productId, quantity);
        eventPublisher.publishEvent(event);
        kafkaTemplate.send("stock.updated.topic", event);
        log.info("Stock update event published for productId {} with quantity {}", productId, quantity);
    }
}

