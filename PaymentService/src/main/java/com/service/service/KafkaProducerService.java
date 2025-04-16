package com.service.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ExecutorService executorService;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.executorService = Executors.newFixedThreadPool(5); // For concurrent async messaging
    }

    /**
     * Synchronous Kafka message sending
     */
    public void sendMessage(String topic, String message) {
        try {
            kafkaTemplate.send(topic, message).get(); // block until acknowledged
            log.info("Sent Kafka message to topic '{}': {}", topic, message);
        } catch (Exception e) {
            log.error("Failed to send Kafka message to topic '{}'", topic, e);
        }
    }

    /**
     * Asynchronous Kafka message sending with Future
     */
    public CompletableFuture<Void> sendMessageAsync(String topic, String message) {
        return CompletableFuture.runAsync(() -> {
            try {
                kafkaTemplate.send(topic, message);
                log.info("Async Kafka message sent to topic '{}': {}", topic, message);
            } catch (Exception e) {
                log.error("Async Kafka send failed for topic '{}'", topic, e);
            }
        }, executorService);
    }

    /**
     * Fire-and-forget non-blocking send using ExecutorService
     */
    public void fireAndForget(String topic, String message) {
        executorService.submit(() -> {
            try {
                kafkaTemplate.send(topic, message);
                log.debug("Fire-and-forget Kafka message queued for topic '{}'", topic);
            } catch (Exception e) {
                log.error("Fire-and-forget Kafka message failed for topic '{}'", topic, e);
            }
        });
    }

    /**
     * Gracefully shutdown the thread pool on application shutdown
     */
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down KafkaProducerService thread pool...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}

