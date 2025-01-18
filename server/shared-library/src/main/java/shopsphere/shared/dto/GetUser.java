package shopsphere_shared.dto;

import jakarta.validation.constraints.NotBlank;

public record GetUser(
        @NotBlank(message = "email is required")
        String email
) {}
