package com.notification.service;

import com.notification.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    private  NotificationService notificationService;
    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void processNotification(NotificationRequest request) {
        log.info("Received notification request: {}", request);
        notificationService.sendNotification(request);
    }
}

