package com.inventory.controller;
import com.inventory.model.StockQuantity;
import com.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.UUID;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private UUID testProductId;
    private final int testQuantity = 10;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
    }

    @Test
    void checkStock_ShouldReturnStockQuantity() {
        // Arrange
        when(inventoryService.getStock(testProductId)).thenReturn(testQuantity);

        // Act
        ResponseEntity<Integer> response = inventoryController.checkStock(testProductId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testQuantity, response.getBody());
        verify(inventoryService, times(1)).getStock(testProductId);
    }

    @Test
    void updateStock_ShouldReturnSuccessMessage() {
        // Arrange
        StockQuantity stockQuantity = new StockQuantity(testQuantity);
        doNothing().when(inventoryService).updateStock(testProductId, stockQuantity);

        // Act
        ResponseEntity<String> response = inventoryController.updateStock(testProductId, stockQuantity);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Stock updated", response.getBody());
        verify(inventoryService, times(1)).updateStock(testProductId, stockQuantity);
    }

    @Test
    void increaseStock_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(inventoryService).increaseStock(testProductId, testQuantity);

        // Act
        ResponseEntity<Void> response = inventoryController.increaseStock(testProductId, testQuantity);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(inventoryService, times(1)).increaseStock(testProductId, testQuantity);
    }

    @Test
    void decreaseStock_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(inventoryService).decreaseStock(testProductId, testQuantity);

        // Act
        ResponseEntity<Void> response = inventoryController.decreaseStock(testProductId, testQuantity);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(inventoryService, times(1)).decreaseStock(testProductId, testQuantity);
    }

    @Test
    void checkStock_WhenServiceThrowsException_ShouldPropagate() {
        // Arrange
        when(inventoryService.getStock(testProductId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> inventoryController.checkStock(testProductId));
        verify(inventoryService, times(1)).getStock(testProductId);
    }

    @Test
    void updateStock_WhenServiceThrowsException_ShouldPropagate() {
        // Arrange
        StockQuantity stockQuantity = new StockQuantity(testQuantity);
        doThrow(new RuntimeException("Update failed")).when(inventoryService).updateStock(testProductId, stockQuantity);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> inventoryController.updateStock(testProductId, stockQuantity));
        verify(inventoryService, times(1)).updateStock(testProductId, stockQuantity);
    }
}
