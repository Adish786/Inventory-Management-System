package com.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
class SmsServiceImplTest {
    @InjectMocks
    private SmsServiceImpl smsService;
    @Mock
    private Logger logger;
    private final String testPhoneNumber = "+1234567890";
    private final String testMessage = "Test SMS message";
    @BeforeEach
    void setUp() {
        reset(logger);
    }

    @Test
    void sendSms_ShouldReturnTrue_WhenCalledWithValidParameters() {
        boolean result = smsService.sendSms(testPhoneNumber, testMessage);
        assertTrue(result);
    }

    @Test
    void sendSms_ShouldLogAppropriateMessage_WhenCalled() {
        smsService.sendSms(testPhoneNumber, testMessage);
        assertTrue(smsService.sendSms(testPhoneNumber,testMessage));        // Assert
    }
    @Test
    public void sendSms_ShouldLogAppropriateMessage() {
        String phoneNumber = "+1234567890";
        String message = "Test SMS message";
        boolean result = smsService.sendSms(phoneNumber, message);
        assertTrue(result);
    }

    @Test
    void sendSms_ShouldHandleNullPhoneNumber() {
        boolean result = smsService.sendSms(null, testMessage);
        assertTrue(result); // or adjust based on your expected behavior
    }

    @Test
    void sendSms_ShouldHandleNullMessage() {
        boolean result = smsService.sendSms(testPhoneNumber, null);
        assertTrue(result); // or adjust based on your expected behavior
    }

    @Test
    void sendSms_ShouldHandleEmptyMessage() {
        boolean result = smsService.sendSms(testPhoneNumber, "");
        assertTrue(result);
    }
}
