package shopsphere_authservice.dto.request;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String token;
}
