package com.inventory.service;

import com.inventory.event.ReportGenerationEvent;
import com.inventory.model.SalesReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ReportGenerationServiceImpl implements ReportGenerationService {
    private final SalesReportService salesReportService;
    private final StockReportService stockReportService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${report.topic}")
    private String reportTopic;

    public ReportGenerationServiceImpl(SalesReportService salesReportService,
                                       StockReportService stockReportService,
                                       KafkaTemplate<String, Object> kafkaTemplate) {
        this.salesReportService = salesReportService;
        this.stockReportService = stockReportService;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Triggers report generation asynchronously using Kafka.
     */
    @Override
    public void generateAllReports(UUID productId) {
        log.info("Publishing report generation event for productId: {}", productId);

        ReportGenerationEvent event = new ReportGenerationEvent(this, productId);
        kafkaTemplate.send(reportTopic, productId.toString(), event);

        log.debug("ReportGenerationEvent published to topic {} for product {}", reportTopic, productId);
    }

    /**
     * Generate stock report (cached) with detailed logging.
     */
    @Override
    @Async
    @Cacheable(value = "stockReports", key = "#productId")
    public void generateStockReport(UUID productId) {
        log.info("Starting stock report generation for product: {}", productId);

        try {
            int stockLevel = stockReportService.getStockForProduct(productId);
            log.info("Stock Report Completed - Product: {}, Stock Level: {}", productId, stockLevel);
        } catch (Exception e) {
            log.error("Error generating stock report for product: {}", productId, e);
        }
    }

    /**
     * Generate sales report (cached) with detailed logging.
     */
    @Override
    @Async
    @Cacheable(value = "salesReports", key = "#productId")
    public void generateSalesReport(UUID productId) {
        log.info("Starting sales report generation for product: {}", productId);

        try {
            SalesReport report = salesReportService.generateSalesReport(productId);
            log.info("Sales Report Completed - Product: {}, Total Sold: {}, Total Revenue: {}",
                    productId, report.getTotalSold(), report.getTotalRevenue());
        } catch (Exception e) {
            log.error("Error generating sales report for product: {}", productId, e);
        }
    }
}
