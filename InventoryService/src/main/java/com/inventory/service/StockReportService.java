package com.inventory.service;

import java.util.UUID;

public interface StockReportService {
    int getStockForProduct(UUID productId);
}
