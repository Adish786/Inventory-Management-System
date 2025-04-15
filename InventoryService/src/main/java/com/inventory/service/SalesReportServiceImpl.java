package com.inventory.service;

import com.inventory.event.SalesReportGeneratedEvent;
import com.inventory.model.Sales;
import com.inventory.model.SalesReport;
import com.inventory.repository.SalesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

@Service
public class SalesReportServiceImpl implements SalesReportService {
    private Logger log = LoggerFactory.getLogger(SalesReportServiceImpl.class);
    private final SalesRepository salesRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public SalesReportServiceImpl(SalesRepository salesRepository, ApplicationEventPublisher eventPublisher, KafkaTemplate<String, Object> kafkaTemplate) {
        this.salesRepository = salesRepository;
        this.eventPublisher = eventPublisher;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Cacheable(value = "products", key = "#productId")
    public SalesReport generateSalesReport(UUID productId) {
        log.info("Generating sales report for productId: {}", productId);

        List<Sales> salesList = salesRepository.findByProductId(productId);
        log.debug("Fetched {} sales records for productId: {}", salesList.size(), productId);

        AtomicInteger totalSold = new AtomicInteger(0);
        DoubleAdder totalRevenue = new DoubleAdder();

        salesList.parallelStream().forEach(sale -> {
            totalSold.addAndGet(sale.getQuantitySold());
            totalRevenue.add(sale.getTotalSales());
        });

        SalesReport report = new SalesReport(productId, totalSold.get(), totalRevenue.doubleValue());
        log.info("Generated report: {}", report);
        eventPublisher.publishEvent(new SalesReportGeneratedEvent(this, report));
        log.debug("Published SalesReportGeneratedEvent for productId: {}", productId);
        kafkaTemplate.send((Message<?>) report);
        log.debug("Sent SalesReport to messaging system for productId: {}", productId);
        return report;
    }
}
