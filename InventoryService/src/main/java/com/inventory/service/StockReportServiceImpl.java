package com.inventory.service;

import com.inventory.event.ProductStockFetchedEvent;
import com.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.util.UUID;
import org.springframework.scheduling.annotation.Async;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class StockReportServiceImpl implements StockReportService {
    private Logger log = LoggerFactory.getLogger(StockReportServiceImpl.class);

    private final InventoryRepository inventoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Custom Thread Pool

    public StockReportServiceImpl(InventoryRepository inventoryRepository,
                                  ApplicationEventPublisher eventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Cacheable(value = "productStockCache", key = "#productId")
    public int getStockForProduct(UUID productId) {
        log.info("Fetching stock for productId: {}", productId);

        int stock = inventoryRepository.findByProductId(productId)
                .map(inventory -> inventory.getStockQuantity().getQuantity())
                .orElse(0);

        log.debug("Stock for productId {}: {}", productId, stock);

        // Publish stock event
        eventPublisher.publishEvent(new ProductStockFetchedEvent(this, productId, stock));
        return stock;
    }

    @Async("stockExecutor") // Ensure this executor is defined in config
    public CompletableFuture<Integer> getStockForProductAsync(UUID productId) {
        log.info("Async fetch initiated for productId: {}", productId);
        try {
            int stock = getStockForProduct(productId);
            return CompletableFuture.completedFuture(stock);
        } catch (Exception ex) {
            log.error("Error fetching stock for productId: {}", productId, ex);
            return CompletableFuture.completedFuture(0); // Fallback
        }
    }

    @Override
    public Map<UUID, Integer> getStockForMultipleProducts(List<UUID> productIds) {
        log.info("Fetching stock for multiple products: {}", productIds);

        List<CompletableFuture<Map.Entry<UUID, Integer>>> futures = productIds.stream()
                .map(productId -> CompletableFuture.supplyAsync(() -> {
                    int stock = getStockForProduct(productId);
                    return Map.entry(productId, stock);
                }, executorService))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        Map<UUID, Integer> stockMap = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        log.debug("Completed stock map fetch: {}", stockMap);
        return stockMap;
    }
}

