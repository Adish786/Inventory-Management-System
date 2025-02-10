package com.inventory.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.LogFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "stock_update", groupId = "inventory-group")
    public void listen(String message) {
     //   log.info("Received message: {}", message);
    }
}
