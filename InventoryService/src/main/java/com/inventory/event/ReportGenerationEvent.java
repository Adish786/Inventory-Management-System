package com.inventory.event;

import org.apache.kafka.clients.consumer.internals.events.ApplicationEvent;

import java.util.UUID;

public class ReportGenerationEvent extends ApplicationEvent {
    private final UUID productId;

    public ReportGenerationEvent(Object source, UUID productId) {
        super((Type) source);
        this.productId = productId;
    }

    public UUID getProductId() {
        return productId;
    }
}

