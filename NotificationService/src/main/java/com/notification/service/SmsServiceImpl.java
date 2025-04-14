package com.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Override
    public boolean sendSms(String phoneNumber, String message) {
        CompletableFuture.runAsync(() -> {
            log.info("Sending SMS to {} with message: {}", phoneNumber, message);
            // Simulate delay (e.g., external API latency)
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("SMS sending thread was interrupted", e);
            }
            // You can integrate actual SMS API here
            log.info("SMS sent to {}", phoneNumber);
        }, executorService);

        // Immediately return true to simulate non-blocking "fire and forget"
        return true;
    }
}

