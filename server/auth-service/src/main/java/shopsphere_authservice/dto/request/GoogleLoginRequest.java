package shopsphere_authservice.dto.request;

import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String idToken;
}
