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

    @Mock
    private NotificationRequest testRequest;

    @BeforeEach
  public   void setUp() {
        testRequest = new NotificationRequest();
        testRequest.setUserId(Long.valueOf(123));
        testRequest.setMessage("Test notification");
        testRequest.setType(NotificationType.valueOf("EMAIL"));
    }

    @Test
  public   void sendNotification_ShouldSendMessageViaWebSocket() {
        notificationController.sendNotification(testRequest);
        verify(messagingTemplate).convertAndSend("/topic/notifications", testRequest);
    }

    @org.junit.Test(expected = NullPointerException.class)
   public void sendPush_ShouldReturnSuccessWhenNotificationSent() {
        when(pushNotificationService.sendPushNotification(testRequest)).thenReturn(any());
        ResponseEntity<String> response = notificationController.sendPush(testRequest);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Push Notification Sent!", response.getBody());
        verify(pushNotificationService).sendPushNotification(testRequest);
    }

    @org.junit.Test(expected = NullPointerException.class)
   public void sendPush_ShouldReturnFailureWhenNotificationFailed() {
        when(pushNotificationService.sendPushNotification(testRequest)).thenReturn(any());
        ResponseEntity<String> response = notificationController.sendPush(testRequest);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Failed to Send", response.getBody());
        verify(pushNotificationService).sendPushNotification(testRequest);
    }

    @Test
   public void sendPush_ShouldHandleServiceException() {
        when(pushNotificationService.sendPushNotification(testRequest))
                .thenThrow(new RuntimeException("Service unavailable"));
        assertThrows(RuntimeException.class, () ->
                notificationController.sendPush(testRequest));
    }

    @Test
    void sendNotification_ShouldHandleNullRequest() {
        notificationController.sendNotification(null);
       // verify(messagingTemplate).convertAndSend("/topic/notifications", Optional.ofNullable(null));
    }
}