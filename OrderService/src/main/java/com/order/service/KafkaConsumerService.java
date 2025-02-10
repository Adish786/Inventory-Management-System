package com.order.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "order_update", groupId = "order-group")
    public void listen(String message) {
     //   log.info("Received message: {}", message);
    }
}
