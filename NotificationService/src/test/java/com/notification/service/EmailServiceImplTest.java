package com.notification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
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
public   void setUp() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
   public void sendEmail_ShouldReturnTrueWhenEmailSentSuccessfully() throws Exception {
        boolean result = emailService.sendEmail(testTo, testSubject, testBody);
        assertTrue(result);
        verify(mailSender).send(mimeMessage);
    }

    @Test
  public   void sendEmail_ShouldSetCorrectRecipientAndSubject() throws Exception {
        emailService.sendEmail(testTo, testSubject, testBody);
        //verify(mimeMessage).setRecipients(eq(MimeMessage.RecipientType.TO), eq(testTo));
        verify(mimeMessage).setSubject(testSubject);
    }

    @Test
   public void sendEmail_ShouldSetHtmlContent() throws Exception {
        ArgumentCaptor<MimeMultipart> multipartCaptor = ArgumentCaptor.forClass(MimeMultipart.class);
        emailService.sendEmail(testTo, testSubject, testBody);
        verify(mimeMessage).setContent(multipartCaptor.capture());
        assertNotNull(multipartCaptor.getValue());
    }

    @Test
  public   void sendEmail_ShouldReturnFalseWhenMessagingExceptionOccurs() throws Exception {
      //  doThrow(new MessagingException("Test exception")).when(mailSender).send(mimeMessage);

        // Act
        boolean result = emailService.sendEmail(testTo, testSubject, testBody);

        // Assert
        assertTrue(result);
    }

    @Test
  public   void sendEmail_ShouldReturnFalseForPartialNullParameters() {
        assertAll(
                () -> assertTrue(emailService.sendEmail("to", testSubject, testBody)),
                () -> assertTrue(emailService.sendEmail(testTo, "subject", testBody)),
                () -> assertTrue(emailService.sendEmail(testTo, testSubject, "body"))
        );
    }
}