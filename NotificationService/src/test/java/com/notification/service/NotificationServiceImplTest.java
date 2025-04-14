package com.notification.service;

import static org.mockito.Mockito.*;

import com.notification.model.Notification;
import com.notification.model.NotificationRequest;
import com.notification.repository.NotificationRepository;
import com.notification.utils.NotificationStatus;
import com.notification.utils.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class NotificationServiceImplTest {

    @Mock
    private EmailService emailService;

    @Mock
    private SmsService smsService;

    @Mock
    private PushNotificationService pushService;

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private NotificationRequest request;
    private Notification notification;

    @BeforeEach
public     void setUp() {
        request = new NotificationRequest();
        request.setUserId(123L);
        request.setType(NotificationType.EMAIL);
        request.setRecipient("test@example.com");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");
        notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setMessage(request.getMessage());
        notification.setStatus(NotificationStatus.PENDING);
    }

    @Test
public     void sendNotification_ShouldSavePendingNotificationFirst() {
        when(repository.save(any(Notification.class))).thenReturn(notification);
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);
        notificationService.sendNotification(request);
        verify(repository, atLeastOnce()).save(argThat(notif ->
                notif.getStatus() == NotificationStatus.PENDING));
    }

    @Test
public     void sendNotification_EmailSuccess_ShouldUpdateToSent() throws Exception {
        when(repository.save(any(Notification.class))).thenReturn(notification);
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);
        notificationService.sendNotification(request);
        Thread.sleep(100);
        verify(repository, atLeastOnce()).save(argThat(notif ->
                notif.getStatus() == NotificationStatus.SENT &&
                        notif.getSentAt() != null));
    }

    @Test
public     void sendNotification_EmailFailure_ShouldUpdateToFailed() throws Exception {
        when(repository.save(any(Notification.class))).thenReturn(notification);
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(false);
        notificationService.sendNotification(request);
        Thread.sleep(100);
        verify(repository, atLeastOnce()).save(argThat(notif ->
                notif.getStatus() == NotificationStatus.FAILED &&
                        notif.getSentAt() == null));
    }

    @Test
public     void sendNotification_SmsSuccess_ShouldCallSmsService() throws Exception {
        request.setType(NotificationType.SMS);
        request.setPhoneNumber("+1234567890");
        when(repository.save(any(Notification.class))).thenReturn(notification);
        when(smsService.sendSms(anyString(), anyString())).thenReturn(true);
        notificationService.sendNotification(request);
        Thread.sleep(100);
        verify(smsService).sendSms("+1234567890", "Test Message");
        verify(repository, atLeastOnce()).save(argThat(notif ->
                notif.getStatus() == NotificationStatus.SENT));
    }

    @Test
public     void sendNotification_PushSuccess_ShouldCallPushService() throws Exception {
        request.setType(NotificationType.PUSH);
        when(repository.save(any(Notification.class))).thenReturn(notification);
        when(pushService.sendPushNotification(any(NotificationRequest.class))).thenReturn(true);
        notificationService.sendNotification(request);
        Thread.sleep(100);
        verify(pushService).sendPushNotification(request);
        verify(repository, atLeastOnce()).save(argThat(notif ->
                notif.getStatus() == NotificationStatus.SENT));
    }

    @Test
public     void sendNotification_Exception_ShouldUpdateToFailed() throws Exception {
        when(repository.save(any(Notification.class))).thenReturn(notification);
        when(emailService.sendEmail(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Service unavailable"));
        notificationService.sendNotification(request);
        Thread.sleep(100);
        verify(repository, atLeastOnce()).save(argThat(notif ->
                notif.getStatus() == NotificationStatus.FAILED));
    }
}