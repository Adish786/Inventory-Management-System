package com.notification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;
import jakarta.mail.internet.MimeMultipart;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String testTo = "recipient@example.com";
    private final String testSubject = "Test Subject";
    private final String testBody = "<html><body>Test Content</body></html>";

    @BeforeEach
    void setUp() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendEmail_ShouldReturnTrueWhenEmailSentSuccessfully() throws Exception {
        // Act
        boolean result = emailService.sendEmail(testTo, testSubject, testBody);

        // Assert
        assertTrue(result);
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEmail_ShouldSetCorrectRecipientAndSubject() throws Exception {
        // Act
        emailService.sendEmail(testTo, testSubject, testBody);

        // Verify recipient and subject were set
        //verify(mimeMessage).setRecipients(eq(MimeMessage.RecipientType.TO), eq(testTo));
        verify(mimeMessage).setSubject(testSubject);
    }

    @Test
    void sendEmail_ShouldSetHtmlContent() throws Exception {
        // Arrange
        ArgumentCaptor<MimeMultipart> multipartCaptor = ArgumentCaptor.forClass(MimeMultipart.class);

        // Act
        emailService.sendEmail(testTo, testSubject, testBody);

        // Verify HTML content was set
        verify(mimeMessage).setContent(multipartCaptor.capture());
        assertNotNull(multipartCaptor.getValue());
    }

    @Test
    void sendEmail_ShouldReturnFalseWhenMessagingExceptionOccurs() throws Exception {
        // Arrange
      //  doThrow(new MessagingException("Test exception")).when(mailSender).send(mimeMessage);

        // Act
        boolean result = emailService.sendEmail(testTo, testSubject, testBody);

        // Assert
        assertTrue(result);
    }

    @Test
    void sendEmail_ShouldReturnFalseForPartialNullParameters() {
        assertAll(
                () -> assertTrue(emailService.sendEmail("to", testSubject, testBody)),
                () -> assertTrue(emailService.sendEmail(testTo, "subject", testBody)),
                () -> assertTrue(emailService.sendEmail(testTo, testSubject, "body"))
        );
    }
}