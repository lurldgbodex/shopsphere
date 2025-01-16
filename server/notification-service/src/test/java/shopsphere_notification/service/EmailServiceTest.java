package shopsphere_notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock private MimeMessage mimeMessage;
    @Mock private JavaMailSender mailSender;
    @InjectMocks private EmailService underTest;

    @Test
    void testSendEmail_success() throws MessagingException {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo("test@example.com");
        helper.setSubject("Test Email Service");
        helper.setText("Text Content", true);

        underTest.sendEmail("test@example.com", "Test Email Service", "Text Content");

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendEmail_exception() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP error"));

        try {
            underTest.sendEmail("test@example.com", "Test subject", "Test content");
        } catch (Exception ignored) {
        }

        verify(mailSender, times(0)).send(any(MimeMessage.class));
    }
}