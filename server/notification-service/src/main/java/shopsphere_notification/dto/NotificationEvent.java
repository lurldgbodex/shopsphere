package shopsphere_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopsphere_notification.enums.EventType;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private EventType eventType;
    private Map<String, Object> payload;
}
