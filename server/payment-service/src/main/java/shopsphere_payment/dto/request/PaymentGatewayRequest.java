package shopsphere_logging.dto.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentGatewayRequest (
    String user_id,
    String order_id,
    BigDecimal amount,
    String currency,
    String paymentMethod
){}

