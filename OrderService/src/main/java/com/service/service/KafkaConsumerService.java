package com.service.service;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaConsumerService {
    private Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(10);  // Thread pool for concurrency
    private final KafkaProducerService kafkaProducerService;  // Injecting Kafka producer to send messages after processing

    @Autowired
    public KafkaConsumerService(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @KafkaListener(topics = "order_update", groupId = "order-group", concurrency = "3")
    public void listen(String message) {
        log.info("Received message: {}", message);

        executor.submit(() -> {
            try {
                MDC.put("requestId", message);  // Log correlation ID for better tracing in logs
                log.info("Processing Kafka message in thread {}: {}", Thread.currentThread().getName(), message);

                // Simulating message processing (e.g., update order status)
                processMessage(message);

                // Send an event to Kafka about order processing completion
                kafkaProducerService.sendMessage("order_events", "OrderProcessed: " + message);

            } catch (Exception e) {
                log.error("Error processing Kafka message in thread {}: {}", Thread.currentThread().getName(), message, e);
            } finally {
                MDC.clear();  // Clear MDC after processing
            }
        });
    }

    private void processMessage(String message) {
        // Simulate actual processing logic here (e.g., updating order status in database)
        log.info("Business logic executed for: {}", message);
        // Example: If the message contains JSON data, parse and process it.
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down KafkaConsumerService executor...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("KafkaConsumerService executor shutdown interrupted", e);
            executor.shutdownNow();
        }
    }
}

