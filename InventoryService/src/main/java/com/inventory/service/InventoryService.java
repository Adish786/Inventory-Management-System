package com.inventory.service;

import com.inventory.model.StockQuantity;

import java.util.UUID;

public interface InventoryService {
    int getStock(UUID productId);

    void updateStock(UUID productId, StockQuantity quantity);

    void updateStock(UUID productId, int quantity, boolean increase);

    void increaseStock(UUID productId, int quantity);
    void decreaseStock(UUID productId, int quantity);
}
