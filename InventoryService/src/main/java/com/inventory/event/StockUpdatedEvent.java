package com.inventory.event;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import java.util.UUID;

@Getter
@Slf4j
public class StockUpdatedEvent extends ApplicationEvent {
    private final UUID productId;
    private final int newStockLevel;

    public StockUpdatedEvent(UUID productId, int newStockLevel) {
        super(productId);
        this.productId = productId;
        this.newStockLevel = newStockLevel;
    }
}

