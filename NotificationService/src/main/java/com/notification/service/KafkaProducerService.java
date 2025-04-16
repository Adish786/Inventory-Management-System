package com.notification.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ExecutorService executor = Executors.newFixedThreadPool(10); // To handle concurrent message sending

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message) {
        log.info("Attempting to send message to topic '{}': {}", topic, message);

        // Using Executor to send message asynchronously
        executor.submit(() -> {
            try {
                kafkaTemplate.send(topic, message);
            } catch (Exception e) {
                // General error handling for unexpected failures
                log.error("Error while sending message to topic '{}': {}", topic, message, e);
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down KafkaProducerService executor...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("KafkaProducerService executor shutdown interrupted", e);
            executor.shutdownNow();
        }
    }
}

