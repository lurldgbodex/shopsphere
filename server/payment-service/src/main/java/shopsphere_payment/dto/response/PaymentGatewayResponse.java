package shopsphere_logging.dto.response;

import lombok.Builder;

@Builder
public record PaymentGatewayResponse(
        String transaction_id,
        String status,
        String gateway_message
) {}
