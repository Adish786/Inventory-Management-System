package com.notification.service;

import com.notification.model.Notification;
import com.notification.model.NotificationRequest;
import com.notification.repository.NotificationRepository;
import com.notification.utils.NotificationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushService;
    private final NotificationRepository repository;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Tune based on load

    public NotificationServiceImpl(EmailService emailService, SmsService smsService, PushNotificationService pushService, NotificationRepository repository) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.pushService = pushService;
        this.repository = repository;
    }
    public void sendNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setMessage(request.getMessage());
        notification.setStatus(NotificationStatus.PENDING);
        repository.save(notification);

        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try {
                return switch (request.getType()) {
                    case EMAIL -> emailService.sendEmail(request.getRecipient(), request.getSubject(), request.getMessage());
                    case SMS -> smsService.sendSms(request.getPhoneNumber(), request.getMessage());
                    case PUSH -> pushService.sendPushNotification(request);
                };
            } catch (Exception e) {
                return false;
            }
        }, executorService);

        future.thenAcceptAsync(isSent -> {
            notification.setStatus(isSent ? NotificationStatus.SENT : NotificationStatus.FAILED);
            notification.setSentAt(isSent ? LocalDateTime.now() : null);
            repository.save(notification);
        }, executorService);
    }
}
