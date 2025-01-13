package shopsphere_order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @NotBlank(message = "product_id is required")
    private String product_id;
    @NotNull(message = "quantity is required")
    private Integer quantity;
    @NotNull(message = "price_per_unit is required")
    private BigDecimal price_per_unit;
}
