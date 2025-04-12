package com.notification.service;

import com.notification.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationProducer {
    private  KafkaTemplate<String, NotificationRequest> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);
    public void sendNotification(NotificationRequest request) {
        log.info("Publishing notification: {}", request);
        kafkaTemplate.send("notification-topic", request);
    }
}

