package shopsphere_productservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shopsphere_productservice.dto.response.PaginatedResponse;
import shopsphere_productservice.dto.response.ProductDto;
import shopsphere_productservice.model.Product;
import shopsphere_productservice.repository.ProductRepository;
import shopsphere_shared.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @InjectMocks private ProductService underTest;
    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
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
    }

    @Nested
    class TestGetProductByID {

        @Test
        void getProductByID_success() {
            when(productRepository.findById("product-id")).thenReturn(Optional.of(product));

            ProductDto response = underTest.getProductById("product-id");

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

            verify(productRepository).findById("product-id");
        }

        @Test
        void getProductById_invalidProductId() {
            when(productRepository.findById(anyString())).thenReturn(Optional.empty());

            Exception ex = assertThrows(NotFoundException.class, () ->
                    underTest.getProductById("product-id"));

            assertEquals("product not found", ex.getMessage());
            verify(productRepository).findById(anyString());
        }
    }

    @Nested
    class TestGetProducts {

        @Test
        void getProducts_getAllProducts() {
            Pageable pageable = PageRequest.of(0, 20);

            List<Product> products = List.of(product);
            Page<Product> paginatedProduct = new PageImpl<>(products, pageable, products.size());

            when(productRepository.findAll(pageable)).thenReturn(paginatedProduct);

            PaginatedResponse<ProductDto> response = underTest.getProducts(null, pageable);

            assertEquals(1, response.getTotalElement());
            assertEquals(20, response.getSize());
            assertEquals(1, response.getTotalPages());
            assertEquals(product.getId(), response.getContent().get(0).getId());
            assertEquals(product.getName(), response.getContent().get(0).getName());
        }

        @Test
        void getProducts_byCategory() {
            Pageable pageable = PageRequest.of(0, 10);

            List<Product> products = List.of(product);
            Page<Product> paginatedProduct = new PageImpl<>(products, pageable, products.size());

            when(productRepository.findByCategory("test", pageable)).thenReturn(paginatedProduct);
            PaginatedResponse<ProductDto> response = underTest.getProducts("test", pageable);

            assertEquals(1, response.getTotalElement());
            assertEquals(10, response.getSize());
            assertEquals(1, response.getTotalPages());
            assertEquals(product.getId(), response.getContent().get(0).getId());
            assertEquals(product.getCategory(), response.getContent().get(0).getCategory());
        }
    }
}