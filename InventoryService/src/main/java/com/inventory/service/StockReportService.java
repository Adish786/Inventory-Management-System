package com.inventory.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StockReportService {
    int getStockForProduct(UUID productId);
     Map<UUID, Integer> getStockForMultipleProducts(List<UUID> productIds);
}
