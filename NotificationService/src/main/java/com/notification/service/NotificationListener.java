package com.notification.service;

import com.notification.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class NotificationListener {

    private final NotificationService notificationService;
    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Customize as needed

    public NotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void processNotification(NotificationRequest request) {
        log.info("Received notification request: {}", request);

        executorService.submit(() -> {
            try {
                notificationService.sendNotification(request);
              //  log.info("Notification processed for: {}", request.getRecipient());
            } catch (Exception e) {
               // log.error("Error processing notification: {}", request, e);
            }
        });
    }
}
