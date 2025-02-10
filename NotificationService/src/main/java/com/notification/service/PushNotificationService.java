package com.notification.service;

import com.notification.model.NotificationRequest;

public interface PushNotificationService {
   boolean sendPushNotification(NotificationRequest request);
}
