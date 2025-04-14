package com.inventory.service;

import com.inventory.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.UUID;
import org.springframework.scheduling.annotation.Async;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StockReportServiceImpl implements StockReportService {
    private final InventoryRepository inventoryRepository;

    public StockReportServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Cacheable("products")
    public int getStockForProduct(UUID productId) {
        return inventoryRepository.findByProductId(productId)
                .map(inventory -> inventory.getStockQuantity().getQuantity())
                .orElse(0);
    }
    @Async
    public CompletableFuture<Integer> getStockForProductAsync(UUID productId) {
        int stock = getStockForProduct(productId);
        return CompletableFuture.completedFuture(stock);
    }
    public Map<UUID, Integer> getStockForMultipleProducts(List<UUID> productIds) {
        List<CompletableFuture<Map.Entry<UUID, Integer>>> futures = productIds.stream()
                .map(productId -> getStockForProductAsync(productId)
                        .thenApply(stock -> Map.entry(productId, stock)))
                .collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
