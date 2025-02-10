package com.inventory.service;

import java.util.UUID;

public interface ReportGenerationService {
    void generateStockReport(UUID productId);
    void generateSalesReport(UUID productId);
}
