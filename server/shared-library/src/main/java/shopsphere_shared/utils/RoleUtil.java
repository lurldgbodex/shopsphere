package shopsphere_shared.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import shopsphere_shared.Role;
import shopsphere_shared.exceptions.ForbiddenException;
import shopsphere_shared.exceptions.MissingHeaderException;

import java.util.List;

@Component
public class RoleUtil {
    public static void verifyRole(HttpHeaders headers, List<Role> roles) {
        if (headers == null) {
            throw new MissingHeaderException("missing needed data payload in header");
        }

        String userRole = headers.getFirst("X-User-Role");

        if (roles.stream().noneMatch(role -> role.name().equalsIgnoreCase(userRole))) {
            throw new ForbiddenException("Access Denied");
        }
    }
}
