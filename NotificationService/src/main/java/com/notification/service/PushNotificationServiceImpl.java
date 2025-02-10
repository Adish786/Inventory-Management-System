package com.notification.service;

import com.notification.model.Notification;
import com.notification.model.NotificationRequest;
import jakarta.mail.Message;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationServiceImpl implements PushNotificationService{

    @Override
    public boolean sendPushNotification(NotificationRequest request) {
        return false;
    }


    /*
     @Override
    public boolean sendPushNotification(NotificationRequest request) {
        try {
            Message message = Message.builder()
                    .setToken(request.getDeviceToken()) // Target device
                    .setNotification(Notification.builder()
                            .setTitle(request.getTitle())
                            .setBody(request.getMessage())
                            .build())
                    .putData("extraInfo", "Some additional data")
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Push Notification Sent: " + response);
            return true;
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
     */
}
