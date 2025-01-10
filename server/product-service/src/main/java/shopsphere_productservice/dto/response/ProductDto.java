package shopsphere_productservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductDto {
    private String id;
    private String name;
    private Integer quantity;
    private Double price;
    private String category;
    private String vendor_id;
    private String description;
    private List<String> images;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
