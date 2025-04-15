package com.product.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaConsumerService {
    private Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @KafkaListener(topics = "product_update", groupId = "product-group", concurrency = "3")
    public void listen(String message) {
        executor.submit(() -> {
            MDC.put("requestId", UUID.randomUUID().toString());
            try {
                log.info("Received Kafka message in thread [{}]: {}", Thread.currentThread().getName(), message);

                // Process the message (simulate EDA / business logic)
                processMessage(message);

                log.info("Completed processing of Kafka message: {}", message);
            } catch (Exception e) {
                log.error("Error processing Kafka message in thread [{}]: {}", Thread.currentThread().getName(), message, e);
            } finally {
                MDC.clear(); // Always clear to avoid leakage
            }
        });
    }

    private void processMessage(String message) {
        // Simulated processing logic (can include JSON parsing, DB update, cache evict/populate)
        log.debug("Starting business logic for message: {}", message);

        // TODO: Real EDA logic here
        // Example: deserialize message -> update ProductService -> update Redis cache
        // e.g., Product product = objectMapper.readValue(message, Product.class);

        log.debug("Finished business logic for message: {}", message);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down KafkaConsumerService executor...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Executor did not terminate in time, forcing shutdown...");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Executor shutdown interrupted: ", e);
            executor.shutdownNow();
        }
    }
}
