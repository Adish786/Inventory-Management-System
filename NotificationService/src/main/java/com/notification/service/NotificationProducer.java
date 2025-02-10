package com.notification.service;

import com.notification.model.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationProducer {
    private  KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    public void sendNotification(NotificationRequest request) {
        //log.info("Publishing notification: {}", request);
        kafkaTemplate.send("notification-topic", request);
    }
}

