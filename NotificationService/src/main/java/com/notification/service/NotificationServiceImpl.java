package com.notification.service;

import com.notification.model.Notification;
import com.notification.model.NotificationRequest;
import com.notification.repository.NotificationRepository;
import com.notification.utils.NotificationStatus;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushService;
    private final NotificationRepository repository;
    private final KafkaProducerService kafkaProducerService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    public NotificationServiceImpl(EmailService emailService,
                                   SmsService smsService,
                                   PushNotificationService pushService,
                                   NotificationRepository repository,
                                   KafkaProducerService kafkaProducerService) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.pushService = pushService;
        this.repository = repository;
        this.kafkaProducerService = kafkaProducerService;
    }
    @Override
    @Cacheable(value = "notifications", key = "#request.userId + '_' + #request.type + '_' + #request.message.hashCode()")
    public void sendNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setMessage(request.getMessage());
        notification.setStatus(NotificationStatus.PENDING);
        repository.save(notification);
        log.info("Sending {} notification to user: {}", request.getType(), request.getUserId());
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try {
                /*
                return switch (request.getType()) {
                    case EMAIL -> emailService.sendEmail(request.getRecipient(), request.getSubject(), request.getMessage());
                    case SMS -> smsService.sendSms(request.getPhoneNumber(), request.getMessage());
                    case PUSH -> pushService.sendPushNotification(request);
                };

                 */
                return true;
            } catch (Exception e) {
                log.error("Exception while sending {} notification to user {}: {}", request.getType(), request.getUserId(), e.getMessage(), e);
                return false;
            }
        }, executorService);

        future.thenAcceptAsync(isSent -> {
            notification.setStatus(isSent ? NotificationStatus.SENT : NotificationStatus.FAILED);
            notification.setSentAt(isSent ? LocalDateTime.now() : null);
            repository.save(notification);

            // Emit event via Kafka for observability or chaining
            String eventMessage = String.format("NotificationEvent: userId=%s, type=%s, status=%s",
                    request.getUserId(), request.getType(), notification.getStatus());

            kafkaProducerService.sendMessage("notification_events", eventMessage);

            log.info("Notification status updated for user {} to {} and event emitted", request.getUserId(), notification.getStatus());
        }, executorService);
    }

    @PreDestroy
    public void shutdownExecutor() {
        log.info("Shutting down NotificationService executor...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                log.warn("Executor forced shutdown due to timeout.");
            }
        } catch (InterruptedException e) {
            log.error("Executor shutdown interrupted: {}", e.getMessage(), e);
            executorService.shutdownNow();
        }
    }
}
