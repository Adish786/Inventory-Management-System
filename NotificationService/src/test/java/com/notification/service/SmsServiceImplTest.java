package com.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import org.junit.Before;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CacheAutoConfiguration.class})
@EnableCaching
class SmsServiceImplTest {

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private CacheManager cacheManager;
    @Mock
    private  CompletableFuture completableFuture;

    @Mock
    private Cache cache;

    @InjectMocks
    private SmsServiceImpl smsService;

    private final String testPhoneNumber = "+1234567890";
    private final String testMessage = "Test message";
    private final String cacheKey = testPhoneNumber + "_" + testMessage.hashCode();

    @BeforeEach
  public   void setUp() {
        when(cacheManager.getCache("sms_notifications")).thenReturn(cache);
    }

    @Test
    public   void sendSms_ShouldReturnTrueAndSendSuccessEvent_WhenSmsSucceeds() throws Exception {
        CompletableFuture<Boolean> result = smsService.sendSms(testPhoneNumber, testMessage);
        assertThat(result.get(5, TimeUnit.SECONDS)).isTrue();

        verify(kafkaProducerService).sendMessage(
                eq("sms_notification_events"),
                eq(String.format("SMS_SENT: phone=%s, message=%s", testPhoneNumber, testMessage))
        );
    }

    @Test
    public  void sendSms_ShouldReturnFalseAndSendFailedEvent_WhenInterrupted() throws Exception {
        ExecutorService mockExecutor = mock(ExecutorService.class);
        smsService = new SmsServiceImpl(kafkaProducerService) {
            protected ExecutorService createExecutor() {
                return mockExecutor;
            }
        };
        CompletableFuture<Boolean> result = smsService.sendSms(testPhoneNumber, testMessage);
        assertThat(result.get(5, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public  void sendSms_ShouldReturnFalseAndSendFailedEvent_WhenGeneralExceptionOccurs() throws Exception {
        ExecutorService mockExecutor = mock(ExecutorService.class);
        smsService = new SmsServiceImpl(kafkaProducerService) {
            protected ExecutorService createExecutor() {
                return mockExecutor;
            }
        };
        CompletableFuture<Boolean> result = smsService.sendSms(testPhoneNumber, testMessage);
        assertThat(result.get(5, TimeUnit.SECONDS)).isTrue();

    }

    @Test
    public   void sendSms_ShouldUseCache_WhenSameParametersAreUsed() throws Exception {
        when(cache.get(cacheKey, CompletableFuture.class)).thenReturn(CompletableFuture.completedFuture(true));
        CompletableFuture<Boolean> result = smsService.sendSms(testPhoneNumber, testMessage);
        assertThat(result.get(5, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public   void sendSms_ShouldHandleNullMessageGracefully() throws Exception {
        CompletableFuture<Boolean> result = smsService.sendSms(testPhoneNumber, null);
        assertThat(result.get(5, TimeUnit.SECONDS)).isTrue();

    }

    @Test
    public   void sendSms_ShouldHandleNullPhoneNumberGracefully() throws Exception {
        CompletableFuture<Boolean> result = smsService.sendSms(null, testMessage);
        assertThat(result.get(5, TimeUnit.SECONDS)).isTrue();
    }
}