package com.service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class KafkaConsumerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);


    @KafkaListener(topics = "stock_update", groupId = "inventory-group")
    public void listen(String message) {
        log.info("Received message: {}", message);
    }
}
