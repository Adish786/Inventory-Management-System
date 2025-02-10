package com.inventory.service;

import com.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class StockReportServiceImpl implements StockReportService {
    private final InventoryRepository inventoryRepository;



    public StockReportServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public int getStockForProduct(UUID productId) {
        return inventoryRepository.findByProductId(productId)
                .map(inventory -> inventory.getStockQuantity().getQuantity())
                .orElse(0);
    }

}
