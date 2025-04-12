package com.notification.controller;

import com.notification.model.NotificationRequest;
import com.notification.service.PushNotificationService;
import com.notification.utils.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationWebSocketControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private PushNotificationService pushNotificationService;

    @InjectMocks
    private NotificationWebSocketController notificationController;

    private NotificationRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new NotificationRequest();
        testRequest.setUserId(Long.valueOf(123));
        testRequest.setMessage("Test notification");
        testRequest.setType(NotificationType.valueOf("EMAIL"));
    }

    @Test
    void sendNotification_ShouldSendMessageViaWebSocket() {
        // Act
        notificationController.sendNotification(testRequest);

        // Assert
        verify(messagingTemplate).convertAndSend("/topic/notifications", testRequest);
    }

    @Test
    void sendPush_ShouldReturnSuccessWhenNotificationSent() {
        // Arrange
        when(pushNotificationService.sendPushNotification(testRequest)).thenReturn(true);

        // Act
        ResponseEntity<String> response = notificationController.sendPush(testRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Push Notification Sent!", response.getBody());
        verify(pushNotificationService).sendPushNotification(testRequest);
    }

    @Test
    void sendPush_ShouldReturnFailureWhenNotificationFailed() {
        // Arrange
        when(pushNotificationService.sendPushNotification(testRequest)).thenReturn(false);

        // Act
        ResponseEntity<String> response = notificationController.sendPush(testRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Failed to Send", response.getBody());
        verify(pushNotificationService).sendPushNotification(testRequest);
    }

    @Test
    void sendPush_ShouldHandleServiceException() {
        // Arrange
        when(pushNotificationService.sendPushNotification(testRequest))
                .thenThrow(new RuntimeException("Service unavailable"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                notificationController.sendPush(testRequest));
    }

    @Test
    void sendNotification_ShouldHandleNullRequest() {
        // Act
        notificationController.sendNotification(null);

        // Assert
       // verify(messagingTemplate).convertAndSend("/topic/notifications", Optional.ofNullable(null));
    }
}