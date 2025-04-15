package com.notification.service;

import com.notification.model.NotificationRequest;

import java.util.concurrent.CompletableFuture;

public interface PushNotificationService {
   CompletableFuture<Boolean> sendPushNotification(NotificationRequest request);
}
