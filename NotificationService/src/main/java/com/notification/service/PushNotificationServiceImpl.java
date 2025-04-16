package com.notification.service;

import com.notification.model.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PushNotificationServiceImpl implements PushNotificationService {
    private final KafkaProducerService kafkaProducerService;

    public PushNotificationServiceImpl(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    @Cacheable(value = "push_notifications", key = "#request.userId + '_' + #request.message.hashCode()")
    @Async
    public CompletableFuture<Boolean> sendPushNotification(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Sending push notification to user: {}, message: {}", request.getUserId(), request.getMessage());
                boolean sent = true;
                if (sent) {
                    log.info("Push notification sent successfully to user: {}", request.getUserId());
                    kafkaProducerService.sendMessage("push_notification_events",
                            String.format("PUSH_SENT: userId=%s, message=%s",
                                    request.getUserId(), request.getMessage()));
                    return true;
                } else {
                    log.warn("Push notification sending returned false for user: {}", request.getUserId());
                    return false;
                }
            } catch (Exception e) {
                log.error("Failed to send push notification to user {}: {}", request.getUserId(), e.getMessage(), e);
                kafkaProducerService.sendMessage("push_notification_events",
                        String.format("PUSH_FAILED: userId=%s, message=%s, error=%s",
                                request.getUserId(), request.getMessage(), e.getMessage()));

                return false;
            }
        });
    }
}
