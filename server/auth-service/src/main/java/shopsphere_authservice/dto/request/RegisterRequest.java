package shopsphere_authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record RegisterRequest (

    @NotBlank(message = "email is required")
    @Email(message = "please provide a valid email")
    String email,

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 with one uppercase, lowercase, number and special character."
    )
    @NotBlank(message = "password is required")
    String password
){}
