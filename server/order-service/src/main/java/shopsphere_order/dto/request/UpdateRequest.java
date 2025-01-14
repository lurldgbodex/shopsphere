package shopsphere_order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import shopsphere_order.enums.OrderStatus;

@Data
public class UpdateRequest {
    @NotNull(message = "status is required")
    private OrderStatus status;
}
