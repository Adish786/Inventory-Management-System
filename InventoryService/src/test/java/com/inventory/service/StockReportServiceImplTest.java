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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;

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
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
        testInventory = new Inventory(testProductId, 50);
        testInventory.setStockQuantity(new StockQuantity(50));
        testInventory = new Inventory(testProductId, 50);
        testInventory.setProductId(testProductId);
        testInventory.setStockQuantity(new StockQuantity(50));
    }

    @Test
    void getStockForProduct_ShouldReturnStockQuantityWhenProductExists() {
        when(inventoryRepository.findByProductId(testProductId))
                .thenReturn(Optional.of(testInventory));
        int result = stockReportService.getStockForProduct(testProductId);
        assertEquals(50, result);
        verify(inventoryRepository).findByProductId(testProductId);
    }

    @Test
    void getStockForProduct_ShouldReturnZeroWhenProductNotFound() {
        when(inventoryRepository.findByProductId(testProductId))
                .thenReturn(Optional.empty());
        int result = stockReportService.getStockForProduct(testProductId);
        assertEquals(0, result);
        verify(inventoryRepository).findByProductId(testProductId);
    }


    @Test
    void getStockForProduct_ShouldHandleRepositoryException() {
        when(inventoryRepository.findByProductId(testProductId))
                .thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () ->
                stockReportService.getStockForProduct(testProductId));
    }

    @Test
    void getStockForProduct_ShouldReturnCorrectValueForZeroStock() {
        testInventory.setStockQuantity(new StockQuantity(0));
        when(inventoryRepository.findByProductId(testProductId))
                .thenReturn(Optional.of(testInventory));
        int result = stockReportService.getStockForProduct(testProductId);
        assertEquals(0, result);
    }
}
