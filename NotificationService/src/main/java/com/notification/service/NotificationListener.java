package com.notification.service;

import com.notification.model.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class NotificationListener {

    private final NotificationService notificationService;
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
