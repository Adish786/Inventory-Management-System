package com.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
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

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

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

    private NotificationRequest emailRequest;
    private NotificationRequest smsRequest;
    private NotificationRequest pushRequest;
    @BeforeEach
    void setUp() {
        // Adjust these parameters to match your actual NotificationRequest constructor
        emailRequest = new NotificationRequest();
        emailRequest.setUserId(1L);
        emailRequest.setType(NotificationType.EMAIL);
        emailRequest.setMessage("Test email message");
        smsRequest = new NotificationRequest();
        smsRequest.setUserId(2L);
        smsRequest.setType(NotificationType.SMS);
        smsRequest.setMessage("Test SMS message");
        pushRequest = new NotificationRequest();
        pushRequest.setUserId(3L);
        pushRequest.setType(NotificationType.PUSH);
        pushRequest.setMessage("Test push message");
    }

    @Test
    void sendNotification_EmailSuccess_ShouldSaveWithSentStatus() {
        // Arrange
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        // Act
        notificationService.sendNotification(emailRequest);

        // Assert
        verify(repository, times(2)).save(argThat(notification -> {
            if (notification.getStatus() == NotificationStatus.PENDING) {
                return true;
            }
            return notification.getStatus() == NotificationStatus.SENT &&
                    notification.getSentAt() != null;
        }));
        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void sendNotification_EmailFailure_ShouldSaveWithFailedStatus() {
        // Arrange
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(false);

        // Act
        notificationService.sendNotification(emailRequest);

        // Assert
        verify(repository, times(2)).save(argThat(notification -> {
            if (notification.getStatus() == NotificationStatus.PENDING) {
                return true;
            }
            return notification.getStatus() == NotificationStatus.FAILED &&
                    notification.getSentAt() == null;
        }));
    }

    @Test
    void sendNotification_SmsSuccess_ShouldSaveWithSentStatus() {
        // Arrange
        when(smsService.sendSms(anyString(), anyString())).thenReturn(true);

        // Act
        notificationService.sendNotification(smsRequest);

        // Assert
        verify(repository, times(2)).save(argThat(notification -> {
            if (notification.getStatus() == NotificationStatus.PENDING) {
                return true;
            }
            return notification.getStatus() == NotificationStatus.SENT &&
                    notification.getSentAt() != null;
        }));
        verify(smsService).sendSms(anyString(), anyString());
    }

    @Test
    void sendNotification_PushSuccess_ShouldSaveWithSentStatus() {
        // Arrange
        when(pushService.sendPushNotification(any(NotificationRequest.class))).thenReturn(true);

        // Act
        notificationService.sendNotification(pushRequest);

        // Assert
        verify(repository, times(2)).save(argThat(notification -> {
            if (notification.getStatus() == NotificationStatus.PENDING) {
                return true;
            }
            return notification.getStatus() == NotificationStatus.SENT &&
                    notification.getSentAt() != null;
        }));
        verify(pushService).sendPushNotification(pushRequest);
    }

}
