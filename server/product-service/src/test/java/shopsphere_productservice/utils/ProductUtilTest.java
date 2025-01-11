package shopsphere_productservice.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import shopsphere_productservice.dto.response.ProductDto;
import shopsphere_productservice.model.Product;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductUtilTest {

    @Test
    void mapToDto() {
        Product product = Product.builder()
                .id("product-id")
                .name("test product")
                .price(25.4)
                .quantity(50)
                .category("Test")
                .vendorId("vendor-id")
                .images(List.of("image-url1", "image-url2"))
                .description("test product description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ProductDto response = ProductUtil.mapToDto(product);

        assertEquals(product.getId(), response.getId());
        assertEquals(product.getName(), response.getName());
        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(product.getImages(), response.getImages());
        assertEquals(product.getQuantity(), response.getQuantity());
        assertEquals(product.getCategory(), response.getCategory());
        assertEquals(product.getVendorId(), response.getVendor_id());
        assertEquals(product.getDescription(), response.getDescription());
        assertEquals(product.getCreatedAt(), response.getCreated_at());
        assertEquals(product.getUpdatedAt(), response.getUpdated_at());
    }
}