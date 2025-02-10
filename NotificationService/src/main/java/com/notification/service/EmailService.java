package com.notification.service;

import com.notification.model.NotificationRequest;

public interface EmailService {
  //  boolean sendEmail(NotificationRequest request);
  boolean sendEmail(String to, String subject, String body);
}
