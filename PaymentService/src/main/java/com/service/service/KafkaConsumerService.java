package com.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.event.PaymentEvent;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Service
public class KafkaConsumerService {
    private Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ApplicationEventPublisher eventPublisher;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public KafkaConsumerService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @KafkaListener(topics = "stock_update", groupId = "inventory-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message) {
        log.info("Kafka message received from topic 'stock_update': {}", message);

        executor.submit(() -> {
            try {
                PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);
                log.debug("Parsed PaymentEvent: {}", event);

                // Optionally publish to internal Spring event listeners
                eventPublisher.publishEvent(event);

                // Further processing
                processPaymentEvent(event);

            } catch (JsonProcessingException e) {
                log.error("Error parsing Kafka message into PaymentEvent: {}", message, e);
            } catch (Exception e) {
                log.error("Unexpected error while processing Kafka message: {}", message, e);
            }
        });
    }

    private void processPaymentEvent(PaymentEvent event) {
        log.info("Processing payment event asynchronously: {}", event);
        // Business logic for processing the event
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
            executor.shutdownNow();
        }
    }
}

