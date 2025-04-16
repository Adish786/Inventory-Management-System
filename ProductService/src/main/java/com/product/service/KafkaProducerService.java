package com.product.service;

import com.product.event.KafkaMessageEvent;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ExecutorService executor;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate,
                                ApplicationEventPublisher eventPublisher) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventPublisher = eventPublisher;
        this.executor = Executors.newFixedThreadPool(5);
    }

    /**
     * Send message asynchronously using thread pool
     */
    public void sendMessageAsync(String topic, String message) {
        executor.submit(() -> {
            try {
                kafkaTemplate.send(topic, message);
                eventPublisher.publishEvent(new KafkaMessageEvent(topic, message));
            } catch (Exception e) {
                log.error("Exception while sending Kafka message asynchronously: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * Send message with CompletableFuture
     */
    public CompletableFuture<Void> sendMessageFuture(String topic, String message) {
        return CompletableFuture.runAsync(() -> {
            try {
                kafkaTemplate.send(topic, message).get();
                log.info("Future-based message sent to topic [{}]: {}", topic, message);
                eventPublisher.publishEvent(new KafkaMessageEvent(topic, message));
            } catch (Exception e) {
                log.error("Exception during future-based send to Kafka: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }, executor);
    }

    /**
     * Send message synchronously
     */
    public void sendMessage(String topic, String message) {
        try {
            kafkaTemplate.send(topic, message).get();
            log.info("Synchronous message sent to topic [{}]: {}", topic, message);
            eventPublisher.publishEvent(new KafkaMessageEvent(topic, message));
        } catch (Exception e) {
            log.error("Synchronous Kafka send failed for topic [{}]: {}", topic, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Graceful shutdown of executor
     */
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down KafkaProducerService executor");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Executor did not shut down in time. Forcing shutdown...");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Interrupted during executor shutdown: {}", e.getMessage());
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
