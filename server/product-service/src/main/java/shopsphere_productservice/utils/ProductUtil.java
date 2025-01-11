package shopsphere_productservice.utils;

import org.springframework.stereotype.Component;
import shopsphere_productservice.dto.response.ProductDto;
import shopsphere_productservice.model.Product;

@Component
public class ProductUtil {

    public static ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .images(product.getImages())
                .quantity(product.getQuantity())
                .category(product.getCategory())
                .vendor_id(product.getVendorId())
                .description(product.getDescription())
                .created_at(product.getCreatedAt())
                .updated_at(product.getUpdatedAt())
                .build();
    }
}
