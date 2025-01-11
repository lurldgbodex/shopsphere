package shopsphere_shared.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import shopsphere_shared.Role;
import shopsphere_shared.exceptions.ForbiddenException;

@Component
public class RoleUtil {
    public static void verifyRole(HttpHeaders headers, Role role) {
        String userRole = headers.getFirst("X-User-Role");
        String requiredRole = role.toString();

        if (!requiredRole.equalsIgnoreCase(userRole)) {
            throw new ForbiddenException("Access Denied");
        }
    }
}
