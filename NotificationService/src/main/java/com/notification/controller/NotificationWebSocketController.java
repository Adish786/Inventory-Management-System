package com.notification.controller;

import com.notification.model.NotificationRequest;
import com.notification.service.PushNotificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.CompletableFuture;

@Controller
@Slf4j
public class NotificationWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final PushNotificationService pushNotificationService;

    public NotificationWebSocketController(SimpMessagingTemplate messagingTemplate, PushNotificationService pushNotificationService) {
        this.messagingTemplate = messagingTemplate;
        this.pushNotificationService = pushNotificationService;
    }

    @MessageMapping("/send")
    @ApiOperation(value = "get message details", notes = "get message details")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public void sendNotification(NotificationRequest request) {
        //log.info("Sending real-time notification: {}", request);
        messagingTemplate.convertAndSend("/topic/notifications", request);
    }


      @PostMapping("/send")
      @ApiOperation(value = "add massage details", notes = "add massage details")
      @ApiResponses(value = { @ApiResponse(code = 201, message = "Success"),
              @ApiResponse(code = 400, message = "Bad Request"),
              @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<String> sendPush(@RequestBody NotificationRequest request) {
          CompletableFuture<Boolean> isSent = pushNotificationService.sendPushNotification(request);
        return ResponseEntity.ok(isSent.join() ? "Push Notification Sent!" : "Failed to Send");
    }

}

