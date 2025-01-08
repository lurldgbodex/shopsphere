package shopsphere_authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
    @NotBlank(message = "email is required")
    String email,

    @NotBlank(message = "password is required")
    String password
) {}
