package com.inventory.service;

import com.inventory.model.SalesReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ReportGenerationServiceImpl implements ReportGenerationService {

    private final SalesReportService salesReportService;
    private final StockReportService stockReportService;

    public ReportGenerationServiceImpl(SalesReportService salesReportService, StockReportService stockReportService) {
        this.salesReportService = salesReportService;
        this.stockReportService = stockReportService;
    }
    public void generateAllReports(UUID productId) {
        CompletableFuture.runAsync(() -> generateAllReports(productId));
        CompletableFuture.runAsync(() -> generateAllReports(productId));
    }
    @Override
    public void generateStockReport(UUID productId) {
        int stockLevel = stockReportService.getStockForProduct(productId);
    }

    @Override
    public void generateSalesReport(UUID productId) {
        SalesReport report = salesReportService.generateSalesReport(productId);
       // log.info("Sales Report for product {}: Total Sold = {}, Total Revenue = {}", productId, report.getTotalSold(), report.getTotalRevenue());
        System.out.println("Sales Report for product {}: Total Sold = {}, Total Revenue = {}");

    }
}
