package shopsphere_gateway.dto;

import lombok.Builder;

@Builder
public record JwtPayload(
    String userId,
    String role,
    String email
){}
