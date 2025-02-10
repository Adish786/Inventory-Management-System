package com.inventory.service;

import com.inventory.model.SalesReport;

import java.util.UUID;

public interface SalesReportService {
    SalesReport generateSalesReport(UUID productId);
}
