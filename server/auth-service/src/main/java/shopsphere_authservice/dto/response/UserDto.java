package shopsphere_authservice.dto.response;

import lombok.Builder;
import lombok.Data;
import shopsphere_authservice.enums.UserRole;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private UserRole role;
    private String google_id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
