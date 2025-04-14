package com.inventory.controller;


import com.inventory.event.StockUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StockUpdateListener {

    private final SimpMessagingTemplate messagingTemplate;

    public StockUpdateListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleStockUpdated(StockUpdatedEvent event) {
        messagingTemplate.convertAndSend("/topic/stock-updates", event);
    }
}

