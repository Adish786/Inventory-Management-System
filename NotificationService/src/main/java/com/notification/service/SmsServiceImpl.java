package com.notification.service;

import com.notification.model.NotificationRequest;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public boolean sendSms(String phoneNumber, String message) {
        System.out.println("Sending SMS to " + phoneNumber + " with message: " + message);
        return true; // Simulate success
    }
}
