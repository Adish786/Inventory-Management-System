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
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final RedisTemplate<String, Integer> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    // Fine-grained lock per product
    private final ConcurrentHashMap<UUID, ReentrantLock> productLocks = new ConcurrentHashMap<>();

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                RedisTemplate<String, Integer> redisTemplate,
                                ApplicationEventPublisher eventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.redisTemplate = redisTemplate;
        this.eventPublisher = eventPublisher;
    }

    private ReentrantLock getLock(UUID productId) {
        return productLocks.computeIfAbsent(productId, id -> new ReentrantLock());
    }

    @Transactional
    @Cacheable(value = "product")
    public int getStock(UUID productId) {
        String cacheKey = "stock_" + productId;
        Integer cachedStock = redisTemplate.opsForValue().get(cacheKey);

        if (cachedStock != null) {
            return cachedStock;
        }

        Inventory inventory = inventoryRepository.findByProductId(productId).orElse(null);

        if (inventory != null) {
            int stock = inventory.getStockQuantity().getQuantity();
            redisTemplate.opsForValue().set(cacheKey, stock);
            return stock;
        }
        return 0;
    }

    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public void updateStock(UUID productId, StockQuantity quantity) {
        ReentrantLock lock = getLock(productId);
        lock.lock();
        try {
            Inventory inventory = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            inventory.setStockQuantity(quantity);
            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);

            redisTemplate.opsForValue().set("stock_" + productId, quantity.getQuantity());
          //  log.info("Stock updated for productId {}: {}", productId, quantity.getQuantity());
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public void updateStock(UUID productId, int quantity, boolean increase) {
        ReentrantLock lock = getLock(productId);
        lock.lock();
        try {
            Inventory inventory = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found in inventory"));

            if (increase) {
                inventory.increaseStock(quantity);
            } else {
                inventory.decreaseStock(quantity);
            }

            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);

            redisTemplate.opsForValue().set("stock_" + productId, inventory.getStockQuantity().getQuantity());

            eventPublisher.publishEvent(new StockUpdatedEvent(productId, inventory.getStockQuantity().getQuantity()));
           // log.info("Stock {} for productId {} by {}. New stock: {}", (increase ? "increased" : "decreased"), productId, quantity, inventory.getStockQuantity().getQuantity());

        } finally {
            lock.unlock();
        }
    }

    @Transactional
    @CacheEvict(value = "quantity", key = "#productId")
    public void increaseStock(UUID productId, int quantity) {
        updateStock(productId, quantity, true);
    }

    @Transactional
    @CacheEvict(value = "quantity", key = "#productId")
    public void decreaseStock(UUID productId, int quantity) {
        updateStock(productId, quantity, false);
    }
}

