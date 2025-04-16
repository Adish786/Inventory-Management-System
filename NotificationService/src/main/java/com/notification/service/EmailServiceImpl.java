package com.notification.service;

import jakarta.annotation.PreDestroy;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final KafkaProducerService kafkaProducerService;  // For event-driven messaging
    private final ExecutorService executor = Executors.newFixedThreadPool(5);  // For concurrent email sending

    public EmailServiceImpl(JavaMailSender mailSender, KafkaProducerService kafkaProducerService) {
        this.mailSender = mailSender;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Async
    public CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String body) {
        try {
            log.info("Sending email asynchronously to: {}", to);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);

            // Send email sent event via Kafka
            executor.submit(() -> kafkaProducerService.sendMessage("notification_events", "EmailSent: " + to));

            log.info("Email successfully sent to {}", to);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);

            // Send failure event via Kafka
            executor.submit(() -> kafkaProducerService.sendMessage("notification_events", "EmailFailed: " + to));

            return CompletableFuture.completedFuture(false);
        }
    }

    @Cacheable(value = "emailNotifications", key = "#to + #subject")
    public boolean sendEmail(String to, String subject, String body) {
        log.info("Initiating email send to: {} with subject: {}", to, subject);
        return sendEmailAsync(to, subject, body).join();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down EmailService executor...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("EmailService executor shutdown interrupted", e);
            executor.shutdownNow();
        }
    }
}

