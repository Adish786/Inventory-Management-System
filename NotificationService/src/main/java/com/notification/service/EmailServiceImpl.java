package com.notification.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Async
    public CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        //    log.info("Email sent to {}", to);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
         //   log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }
    @Cacheable("notification")
    public boolean sendEmail(String to, String subject, String body) {
        return sendEmailAsync(to, subject, body).join();
    }
}

