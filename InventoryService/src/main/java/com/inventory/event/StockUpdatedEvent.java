package com.inventory.event;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.util.UUID;

@Getter
public class StockUpdatedEvent extends ApplicationEvent {
    private final UUID productId;
    private final int newStockLevel;

    public StockUpdatedEvent(UUID productId, int newStockLevel) {
        super(productId);
        this.productId = productId;
        this.newStockLevel = newStockLevel;
    }
}

