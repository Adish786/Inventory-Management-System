package com.inventory.service;

import com.inventory.model.SalesReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSalesReportProducer implements MessageProducer {
    private Logger log = LoggerFactory.getLogger(KafkaSalesReportProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${sales-report-topic}")
    private String topic;
    public KafkaSalesReportProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendSalesReport(SalesReport report) {
        kafkaTemplate.send(topic, report.getProductId().toString(), report);
        log.info("SalesReport sent to Kafka topic {}: {}", topic, report);
    }
}
