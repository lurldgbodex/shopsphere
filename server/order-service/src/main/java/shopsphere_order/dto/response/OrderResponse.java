package shopsphere_order.dto.response;

import lombok.Builder;
import shopsphere_order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderResponse (
    UUID id,
    String user_id,
    OrderStatus status,
    BigDecimal total_price,
    List<ItemResponse> items,
    LocalDateTime created_at,
    LocalDateTime updated_at
){}
