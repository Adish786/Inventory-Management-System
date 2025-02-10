package com.notification.service;

import com.notification.model.NotificationRequest;

public interface NotificationService {

    void sendNotification(NotificationRequest request);
}
