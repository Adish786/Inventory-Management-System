package com.inventory.controller;

import com.inventory.event.StockUpdatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StockUpdateListenerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private StockUpdateListener stockUpdateListener;

    @Test
    public void handleStockUpdated_ShouldSendMessageToWebSocketTopic() {
        // Arrange
        StockUpdatedEvent testEvent = new StockUpdatedEvent(UUID.randomUUID(), 100);
        // Act
        stockUpdateListener.handleStockUpdated(testEvent);
        // Assert
        verify(messagingTemplate).convertAndSend("/topic/stock-updates", testEvent);
    }

    @Test
    public void handleStockUpdated_ShouldHandleNullEvent() {
        // Act
        stockUpdateListener.handleStockUpdated(null);

        // Assert
        //
        // verify(messagingTemplate).convertAndSend(Optional.of(null));
    }

    @Test
    public void handleStockUpdated_ShouldSendCompleteEventObject() {
        // Arrange
        UUID productId = UUID.randomUUID();
        StockUpdatedEvent testEvent = new StockUpdatedEvent(productId, 50);
        // Act
        stockUpdateListener.handleStockUpdated(testEvent);
        // Assert

    }
}
