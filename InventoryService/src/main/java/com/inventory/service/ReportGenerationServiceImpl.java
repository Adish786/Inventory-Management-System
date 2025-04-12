package com.inventory.service;

import com.inventory.model.SalesReport;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class ReportGenerationServiceImpl implements ReportGenerationService{

    private final SalesReportService salesReportService;
    private final StockReportService stockReportService;

    public ReportGenerationServiceImpl(SalesReportService salesReportService, StockReportService stockReportService) {
        this.salesReportService = salesReportService;
        this.stockReportService = stockReportService;
    }

    @Cacheable("products")
    public void generateStockReport(UUID productId) {
        int stockLevel = stockReportService.getStockForProduct(productId);
        System.out.println("Stock Level for product " + productId + ": " + stockLevel);
    }
    @Cacheable("products")
    public void generateSalesReport(UUID productId) {
        SalesReport report = salesReportService.generateSalesReport(productId);
        System.out.println("Sales Report for product " + productId + ": Total Sold = " + report.getTotalSold() +
                ", Total Revenue = " + report.getTotalRevenue());
    }


}
