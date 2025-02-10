package com.notification.service;

import com.notification.model.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationListener {
    private  NotificationService notificationService;

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void processNotification(NotificationRequest request) {
        //log.info("Received notification request: {}", request);
        notificationService.sendNotification(request);
    }
}

