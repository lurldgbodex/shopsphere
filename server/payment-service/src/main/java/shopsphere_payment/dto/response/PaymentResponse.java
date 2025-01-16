package shopsphere_payment.dto.response;

import lombok.Builder;
import shopsphere_payment.enums.PaymentStatus;

import java.util.UUID;

@Builder
public record PaymentResponse (
   UUID payment_id,
   PaymentStatus status,
   String message
) {}
