package shopsphere_notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shopsphere_notification.dto.NotificationEvent;
import shopsphere_notification.enums.EventType;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    @Mock private EmailService emailService;
    @Mock private TemplateService templateService;
    @InjectMocks private NotificationListener underTest;

    @Test
    void testHandleNotificationEvent_accountCreated() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "test-user");
        payload.put("email", "test@example.com");
        payload.put("subject", "Account Created");

        NotificationEvent event = new NotificationEvent();
        event.setEventType(EventType.ACCOUNT_CREATED);
        event.setPayload(payload);

        String notificationContent = templateService.renderTemplate("account_created", payload);

        underTest.handleNotificationEvent(event);

        verify(emailService).sendEmail("test@example.com", "Account Created", notificationContent);
    }

    @Test
    void testHandleNotificationEvent_orderPlaced() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "test-user");
        payload.put("email", "test@example.com");
        payload.put("subject", "Order placed");

        NotificationEvent event = new NotificationEvent();
        event.setEventType(EventType.ORDER_COMPLETED);
        event.setPayload(payload);

        String notificationContent = templateService.renderTemplate("order_placed", payload);

        underTest.handleNotificationEvent(event);

        verify(emailService).sendEmail("test@example.com", "Order placed", notificationContent);
    }

    @Test
    void testHandleNotificationEvent_unKnowEvent() {
        NotificationEvent invalidEvent = new NotificationEvent();

        underTest.handleNotificationEvent(invalidEvent);

        verifyNoMoreInteractions(emailService);
        verifyNoMoreInteractions(templateService);
    }
}