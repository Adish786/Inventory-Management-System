package com.product.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "product_update", groupId = "product-group")
    public void listen(String message) {
       // log.info("Received message: {}", message);
    }
}
