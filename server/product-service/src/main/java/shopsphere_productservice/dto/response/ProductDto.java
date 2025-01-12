package shopsphere_productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
