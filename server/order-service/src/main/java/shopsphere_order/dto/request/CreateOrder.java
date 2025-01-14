package shopsphere_order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateOrder(
        @NotBlank(message = "vendor_id is required")
        String vendor_id,
        @NotNull(message = "items is required")
        List<ItemRequest> items
) {}
