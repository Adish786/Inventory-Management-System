package com.inventory.service;

import com.inventory.event.StockUpdatedEvent;
import com.inventory.model.Inventory;
import com.inventory.model.StockQuantity;
import com.inventory.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final RedisTemplate<String, Integer> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, RedisTemplate<String, Integer> redisTemplate, ApplicationEventPublisher eventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.redisTemplate = redisTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public int getStock(UUID productId) {
        String cacheKey = "stock_" + productId;
        Integer cachedStock = redisTemplate.opsForValue().get(cacheKey);
        if (cachedStock != null) {
            return cachedStock;
        }
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElse(null);

        if (inventory != null) {
            redisTemplate.opsForValue().set(cacheKey, inventory.getStockQuantity().getQuantity());
            return inventory.getStockQuantity().getQuantity();
        }
        return 0;
    }


    @Transactional
    public void updateStock(UUID productId, StockQuantity quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        inventory.setStockQuantity(quantity);
        inventory.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inventory);

        // Update Cache
        redisTemplate.opsForValue().set("stock_" + productId, 0);

    }
/*
    @KafkaListener(topics = "stock_update", groupId = "inventory")
    public void handleStockUpdate(StockUpdateEvent event) {
        inventoryService.updateStock(event.getId(), event.getQuantity());
    }
  */

    @Transactional
    public void updateStock(UUID productId, int quantity, boolean increase) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in inventory"));

        if (increase) {
            inventory.increaseStock(quantity);
        } else {
            inventory.decreaseStock(quantity);
        }

        inventoryRepository.save(inventory);
        // Publish event for real-time updates
        eventPublisher.publishEvent(new StockUpdatedEvent(productId, inventory.getStockQuantity().getQuantity()));
    }

    public void increaseStock(UUID productId, int quantity) {
       updateStock(productId, quantity, true);
    }

    public void decreaseStock(UUID productId, int quantity) {
       updateStock(productId, quantity, false);
    }
}

