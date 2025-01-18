package shopsphere.shared.utils;

import org.springframework.http.HttpHeaders;
import shopsphere.shared.dto.HeaderPayload;

public class HeaderUtil {

    public static HeaderPayload payload(HttpHeaders headers) {
        String userId = headers.getFirst("X-User-Id");
        String userRole = headers.getFirst("x-User-Role");
        return new HeaderPayload(userId, userRole);
    }
}
