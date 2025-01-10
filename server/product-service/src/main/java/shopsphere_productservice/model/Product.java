package shopsphere_productservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
@CompoundIndexes({
        @CompoundIndex(name = "category_idx", def = "{'category': 1}"),
        @CompoundIndex(name = "vendor_idx", def = "{'vendorId': 1}")
})
public class Product {
    @Id
    private String id;
    private String name;
    private int quantity;
    private double price;
    private String category;
    private String vendorId;
    private String description;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
