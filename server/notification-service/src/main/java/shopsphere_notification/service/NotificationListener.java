package shopsphere_notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import shopsphere_notification.dto.NotificationEvent;
import shopsphere_notification.enums.EventType;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final TemplateService templateService;
    private final EmailService emailService;

    @RabbitListener(queues = "${rabbitmq.notification.queue}")
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("Received event: " + event);

        EventType eventType = event.getEventType();
        Map<String, Object> payload = event.getPayload();

        String templateName = getTemplateName(eventType);
        if (templateName != null) {
            String email = payload.get("email").toString();
            String subject = payload.get("subject").toString();

            String notificationContent = templateService.renderTemplate(templateName, payload);

            emailService.sendEmail(email, subject, notificationContent);
        }
    }

    private String getTemplateName(EventType eventType) {
        return switch (eventType) {
            case ACCOUNT_CREATED -> "account_created";
            case ORDER_COMPLETED -> "order_placed";
        };
    }
}
