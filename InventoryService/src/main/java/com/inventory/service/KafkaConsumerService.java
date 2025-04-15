package com.inventory.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class KafkaConsumerService {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    @KafkaListener(topics = "product_update", groupId = "product-group", concurrency = "3")
    public void listen(String message) {
        executor.submit(() -> {
            try {
             //   log.info("Processing Kafka message in thread {}: {}", Thread.currentThread().getName(), message);
                // TODO: Your actual business logic here
                processMessage(message);
            } catch (Exception e) {
              //  log.error("Error processing Kafka message: {}", message, e);
            }
        });
    }
    private void processMessage(String message) {
        // Simulate processing logic
        //log.info("Business logic executed for: {}", message);
        // For example, parsing JSON, updating DB, etc.
    }

    @PreDestroy
    public void shutdown() {
      //  log.info("Shutting down KafkaConsumerService executor...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}

