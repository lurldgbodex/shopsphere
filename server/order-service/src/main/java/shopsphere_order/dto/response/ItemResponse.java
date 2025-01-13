package shopsphere_order.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemResponse(
   String product_id,
   int quantity,
   BigDecimal price_per_unit,
   BigDecimal total_price
) {}
