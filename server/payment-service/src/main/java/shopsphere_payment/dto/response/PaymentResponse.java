package shopsphere_logging.dto.response;

import lombok.Builder;
import shopsphere_logging.enums.PaymentStatus;

import java.util.UUID;

@Builder
public record PaymentResponse (
   UUID payment_id,
   PaymentStatus status,
   String message
) {}
