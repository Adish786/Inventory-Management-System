package com.product.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendMessageAsync(String topic, String message) {
        executor.submit(() -> {
            try {
                kafkaTemplate.send(topic, message);
            } catch (Exception e) {
               // log.error("Exception while sending Kafka message: ", e);
            }
        });
    }
    public CompletableFuture<Void> sendMessageFuture(String topic, String message) {
        return CompletableFuture.runAsync(() -> {
            try {
                kafkaTemplate.send(topic, message);
            } catch (Exception e) {
                //log.error("Async Kafka exception: ", e);
            }
        }, executor);
    }
    public void sendMessage(String topic, String message) {
        try {
            kafkaTemplate.send(topic, message);
          //  log.info("Sent message synchronously to topic {}: {}", topic, message);
        } catch (Exception e) {
          //  log.error("Error sending Kafka message synchronously: ", e);
        }
    }

    @PreDestroy
    public void shutdown() {
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

