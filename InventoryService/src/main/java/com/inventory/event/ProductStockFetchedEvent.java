package com.inventory.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class ProductStockFetchedEvent extends ApplicationEvent {
    private final UUID productId;
    private final int stock;

    public ProductStockFetchedEvent(Object source, UUID productId, int stock) {
        super(source);
        this.productId = productId;
        this.stock = stock;
    }

    public UUID getProductId() { return productId; }
    public int getStock() { return stock; }
}

