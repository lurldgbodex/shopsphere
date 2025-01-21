package shopsphere_logging.dto.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentRequest(
        String order_id,
        BigDecimal amount,
        String paymentMethod,
        String currency
) {}
