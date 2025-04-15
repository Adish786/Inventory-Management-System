package com.inventory.event;

import com.inventory.service.ReportGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class ReportGenerationEventListener {

    private final ReportGenerationService reportGenerationService;

    public ReportGenerationEventListener(ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }

    @Async
    @EventListener
    public void handleReportEvent(ReportGenerationEvent event) {
        UUID productId = event.getProductId();
        //log.info("Handling report event for product: {}", productId);
        reportGenerationService.generateSalesReport(productId);
        reportGenerationService.generateStockReport(productId);
    }
}

