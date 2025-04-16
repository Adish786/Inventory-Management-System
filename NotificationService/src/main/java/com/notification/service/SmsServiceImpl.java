package com.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    private final KafkaProducerService kafkaProducerService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Tune per load

    public SmsServiceImpl(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    @Cacheable(value = "sms_notifications", key = "#phoneNumber + '_' + #message.hashCode()")
    @Async
    public CompletableFuture<Boolean> sendSms(String phoneNumber, String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Attempting to send SMS to [{}] with message: {}", phoneNumber, message);

                // Simulate delay (replace with real SMS API integration)
                Thread.sleep(2000);

                // Simulated success
                log.info("SMS successfully sent to [{}]", phoneNumber);

                // Emit success event to Kafka
                kafkaProducerService.sendMessage("sms_notification_events",
                        String.format("SMS_SENT: phone=%s, message=%s", phoneNumber, message));

                return true;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("SMS sending thread interrupted for phone: {}", phoneNumber, e);

                kafkaProducerService.sendMessage("sms_notification_events",
                        String.format("SMS_FAILED: phone=%s, error=%s", phoneNumber, e.getMessage()));

                return false;

            } catch (Exception e) {
                log.error("Failed to send SMS to [{}]: {}", phoneNumber, e.getMessage(), e);

                kafkaProducerService.sendMessage("sms_notification_events",
                        String.format("SMS_FAILED: phone=%s, error=%s", phoneNumber, e.getMessage()));

                return false;
            }
        }, executorService);
    }
}
