package com.notification.service;

import java.util.concurrent.CompletableFuture;

public interface SmsService {
   // boolean sendSms(NotificationRequest request);
   CompletableFuture<Boolean> sendSms(String phoneNumber, String message);
}
