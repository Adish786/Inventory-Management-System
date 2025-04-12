package com.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
@Service
public class SmsServiceImpl implements SmsService {
    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);
    @Override
    public boolean sendSms(String phoneNumber, String message) {
        log.info("Sending SMS to " + phoneNumber + " with message: " + message);
        return true; // Simulate success
    }
}
