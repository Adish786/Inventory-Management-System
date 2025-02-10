package com.notification.service;

import com.notification.model.NotificationRequest;

public interface SmsService {
   // boolean sendSms(NotificationRequest request);
   boolean sendSms(String phoneNumber, String message);
}
