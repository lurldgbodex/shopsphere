package shopsphere_order.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import shopsphere_order.enums.OrderStatus;

@Data
public class UpdateRequest {
    @NotBlank(message = "status is required")
    private OrderStatus status;
}
