package com.inventory.service;

import com.inventory.model.Inventory;
import com.inventory.model.StockQuantity;
import com.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockReportServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private StockReportServiceImpl stockReportService;

    @Mock
    private UUID testProductId;
    @Mock
    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
        // Create Inventory with required constructor arguments
        testInventory = new Inventory(testProductId, 50); // productId and initial quantity
        testInventory.setStockQuantity(new StockQuantity(50)); // Additional setup if needed
        testInventory = new Inventory(testProductId, 50); // Using the proper constructor
        testInventory.setProductId(testProductId);
        testInventory.setStockQuantity(new StockQuantity(50));
    }

    @Test
    void getStockForProduct_ShouldReturnStockQuantityWhenProductExists() {
        // Arrange
        when(inventoryRepository.findByProductId(testProductId))
                .thenReturn(Optional.of(testInventory));

        // Act
        int result = stockReportService.getStockForProduct(testProductId);

        // Assert
        assertEquals(50, result);
        verify(inventoryRepository).findByProductId(testProductId);
    }

    @Test
    void getStockForProduct_ShouldReturnZeroWhenProductNotFound() {
        // Arrange
        when(inventoryRepository.findByProductId(testProductId))
                .thenReturn(Optional.empty());

        // Act
        int result = stockReportService.getStockForProduct(testProductId);

        // Assert
        assertEquals(0, result);
        verify(inventoryRepository).findByProductId(testProductId);
    }


    @Test
    void getStockForProduct_ShouldHandleRepositoryException() {
        // Arrange
        when(inventoryRepository.findByProductId(testProductId))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                stockReportService.getStockForProduct(testProductId));
    }

    @Test
    void getStockForProduct_ShouldReturnCorrectValueForZeroStock() {
        // Arrange
        testInventory.setStockQuantity(new StockQuantity(0));
        when(inventoryRepository.findByProductId(testProductId))
                .thenReturn(Optional.of(testInventory));

        // Act
        int result = stockReportService.getStockForProduct(testProductId);

        // Assert
        assertEquals(0, result);
    }
}
