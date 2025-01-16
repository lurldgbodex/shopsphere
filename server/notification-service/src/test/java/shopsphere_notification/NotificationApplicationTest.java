package shopsphere_notification;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import shopsphere_notification.config.TestConfig;
import shopsphere_notification.dto.NotificationEvent;
import shopsphere_notification.enums.EventType;
import shopsphere_notification.service.EmailService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import(TestConfig.class)
@ExtendWith(SpringExtension.class)
class NotificationApplicationTest {

    @MockitoBean
    private EmailService emailService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void testHandleEmailNotification() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", "test@example.com");
        payload.put("username", "tester");
        payload.put("subject", "Account Created");

        NotificationEvent event = new NotificationEvent();
        event.setEventType(EventType.ACCOUNT_CREATED);
        event.setPayload(payload);

        rabbitTemplate.convertAndSend(TestConfig.TEST_QUEUE, event);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                verify(emailService).sendEmail(eq("test@example.com"), eq("Account Created"), anyString()));
    }
}