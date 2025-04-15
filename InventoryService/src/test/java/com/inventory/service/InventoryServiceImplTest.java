package com.inventory.service;

import com.inventory.event.StockUpdatedEvent;
import com.inventory.model.Inventory;
import com.inventory.model.StockQuantity;
import com.inventory.repository.InventoryRepository;
import java.lang.RuntimeException;

import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;
    @Mock
    private ValueOperations<String, Integer> valueOperations;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private InventoryServiceImpl inventoryService;
    @Mock
    private KafkaTemplate kafkaTemplate;
    @Mock
    private UUID testProductId;
    @Mock
    private Inventory testInventory;
    @Mock
    private StockUpdatedEvent event;
    private final int testQuantity = 10;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
        testInventory = new Inventory(testProductId, testQuantity);
        testInventory.setLastUpdated(LocalDateTime.now());
        testInventory = new Inventory(testProductId, new StockQuantity(testQuantity).getQuantity());
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    }

    @Test
    void getStock_ShouldReturnCachedValue() {
        String cacheKey = "stock_" + testProductId;
        when(valueOperations.get(cacheKey)).thenReturn(testQuantity);
        int result = inventoryService.getStock(testProductId);
        assertEquals(testQuantity, result);
        verify(valueOperations).get(cacheKey);
        verifyNoInteractions(inventoryRepository);
    }

    @Test
    void getStock_ShouldReturnDatabaseValueWhenCacheMiss() {
        String cacheKey = "stock_" + testProductId;
        when(valueOperations.get(cacheKey)).thenReturn(null);
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(testInventory));
        int result = inventoryService.getStock(testProductId);
        assertEquals(testQuantity, result);
        verify(valueOperations).get(cacheKey);
        verify(inventoryRepository).findByProductId(testProductId);
        verify(valueOperations).set(cacheKey, testQuantity);
    }

    @Test
    void getStock_ShouldReturnZeroWhenProductNotFound() {
        String cacheKey = "stock_" + testProductId;
        when(valueOperations.get(cacheKey)).thenReturn(null);
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.empty());
        int result = inventoryService.getStock(testProductId);
        assertEquals(0, result);
        verify(valueOperations).get(cacheKey);
        verify(inventoryRepository).findByProductId(testProductId);
        verify(valueOperations, never()).set(anyString(), anyInt());
    }

    @Test
    void updateStock_ShouldUpdateInventoryAndCache() {
        StockQuantity newQuantity = new StockQuantity(20);
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(testInventory));
        inventoryService.updateStock(testProductId, newQuantity);
        verify(inventoryRepository).findByProductId(testProductId);
        verify(inventoryRepository).save(testInventory);
        assertEquals(newQuantity, testInventory.getStockQuantity());
        assertNotNull(testInventory.getLastUpdated());
    }



    @Test
    void increaseStock_ShouldUpdateInventoryAndPublishEvent() {
        int increaseAmount = 5;
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(testInventory));
        inventoryService.increaseStock(testProductId, increaseAmount);
        verify(inventoryRepository).save(testInventory);
        assertEquals(testQuantity + increaseAmount, testInventory.getStockQuantity().getQuantity());
    }

    @Test
    void decreaseStock_ShouldUpdateInventoryAndPublishEvent() {
        int decreaseAmount = 3;
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(testInventory));
        inventoryService.decreaseStock(testProductId, decreaseAmount);
        verify(inventoryRepository).save(testInventory);
        assertEquals(testQuantity - decreaseAmount, testInventory.getStockQuantity().getQuantity());
    }

    @Test
    void decreaseStock_ShouldThrowWhenInsufficientStock() {
        int decreaseAmount = testQuantity + 1;
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(testInventory));
        assertThrows(IllegalArgumentException.class, () ->
                inventoryService.decreaseStock(testProductId, decreaseAmount));
        verify(inventoryRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
