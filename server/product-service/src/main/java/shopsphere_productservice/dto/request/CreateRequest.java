package shopsphere_productservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateRequest {
    @NotBlank(message = "name is required")
    private String name;
    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be more that 0")
    private int quantity;
    @NotNull(message = "price is required")
    private double price;
    @NotBlank(message = "category is required")
    private String category;
    @NotBlank(message = "description is required")
    private String description;
    @NotBlank(message = "images is required")
    private List<String> images;
}
