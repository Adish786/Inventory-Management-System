package com.notification.service;

import com.notification.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class NotificationProducer {

    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Tune pool size based on load

    public NotificationProducer(KafkaTemplate<String, NotificationRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotification(NotificationRequest request) {
        executorService.submit(() -> {
            try {
                log.info("Publishing notification: {}", request);
                kafkaTemplate.send("notification-topic", request);
               // log.info("Notification published: {}", request.getRecipient());
            } catch (Exception e) {
              //  log.error("Failed to publish notification: {}", request, e);
            }
        });
    }
}


