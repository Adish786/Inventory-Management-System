package com.notification.service;

import com.notification.model.Notification;
import com.notification.model.NotificationRequest;
import com.notification.repository.NotificationRepository;
import com.notification.utils.NotificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushService;
    private final NotificationRepository repository;

    public NotificationServiceImpl(EmailService emailService, SmsService smsService, PushNotificationService pushService, NotificationRepository repository) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.pushService = pushService;
        this.repository = repository;
    }
@Cacheable("notification")
    public void sendNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setMessage(request.getMessage());
        notification.setStatus(NotificationStatus.PENDING);
        repository.save(notification);
        boolean isSent = switch (request.getType()) {
            case EMAIL -> emailService.sendEmail("","","");
            case SMS -> smsService.sendSms("","");
            case PUSH -> pushService.sendPushNotification(request);
        };
        notification.setStatus(isSent ? NotificationStatus.SENT : NotificationStatus.FAILED);
        notification.setSentAt(isSent ? LocalDateTime.now() : null);
        repository.save(notification);
    }

}
