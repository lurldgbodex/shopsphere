package shopsphere_productservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import shopsphere_productservice.dto.request.CreateRequest;
import shopsphere_productservice.dto.response.ProductDto;
import shopsphere_productservice.model.Product;
import shopsphere_productservice.repository.ProductRepository;
import shopsphere_shared.exceptions.ForbiddenException;
import shopsphere_shared.exceptions.MissingHeaderException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorProductServiceTest {

    @Mock private ProductRepository productRepository;
    @InjectMocks private VendorProductService underTest;

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.set("X-User-Role", "vendor");
        headers.set("X-User-Id", "user-id");
    }

    @Nested
    class CreateProductTests {

        private CreateRequest createRequest;

        @BeforeEach
        void setUpCreateProduct() {
            createRequest = CreateRequest.builder()
                    .name("Test Product")
                    .category("Test")
                    .price(15.5)
                    .quantity(10)
                    .description("Testing create Product")
                    .images(List.of("test-product.url1", "test-product.url2"))
                    .build();
        }

        @Test
        void createProduct_success() {
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
                Product product = invocation.getArgument(0);
                return Product.builder()
                        .id("product-id")
                        .name(product.getName())
                        .price(product.getPrice())
                        .images(product.getImages())
                        .vendorId(product.getVendorId())
                        .category(product.getCategory())
                        .quantity(product.getQuantity())
                        .description(product.getDescription())
                        .createdAt(product.getCreatedAt())
                        .updatedAt(product.getUpdatedAt())
                        .build();
            });

            ProductDto response = underTest.createProduct(createRequest, headers);
            ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
            verify(productRepository).save(productArgumentCaptor.capture());

            Product capturedProduct = productArgumentCaptor.getValue();

            assertEquals("user-id", capturedProduct.getVendorId());
            assertEquals(createRequest.getName(), capturedProduct.getName());
            assertEquals(createRequest.getPrice(), capturedProduct.getPrice());
            assertEquals(createRequest.getImages(), capturedProduct.getImages());
            assertEquals(createRequest.getQuantity(), capturedProduct.getQuantity());
            assertEquals(createRequest.getCategory(), capturedProduct.getCategory());
            assertEquals(createRequest.getDescription(), capturedProduct.getDescription());

            assertEquals(capturedProduct.getName(), response.getName());
            assertEquals(capturedProduct.getPrice(), response.getPrice());
            assertEquals(capturedProduct.getQuantity(), response.getQuantity());
            assertEquals(capturedProduct.getVendorId(), response.getVendor_id());
            assertEquals(capturedProduct.getCreatedAt(), response.getCreated_at());
            assertEquals(capturedProduct.getUpdatedAt(), response.getUpdated_at());

            verify(productRepository).save(any(Product.class));
        }

        @Test
        void createProduct_UserNotVendor() {
            headers = new HttpHeaders();
            headers.set("X-User-Role", "user");

            Exception ex = assertThrows(ForbiddenException.class, () ->
                    underTest.createProduct(createRequest, headers));

            assertEquals("Access Denied", ex.getMessage());

            verify(productRepository, never()).save(any(Product.class));
        }


        @Test
        void createProduct_NoUserIdInHeader() {
            headers = new HttpHeaders();
            headers.set("X-User-Role", "vendor");

            Exception ex = assertThrows(MissingHeaderException.class, () ->
                    underTest.createProduct(createRequest, headers));

            assertEquals("missing required data - userID", ex.getMessage());

            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Nested
    class UpdateProductTests {

        private Product product;

        @BeforeEach
        void setUp() {
            product = Product.builder()
                    .id("product-id")
                    .name("Test product")
                    .price(10)
                    .category("Testing")
                    .quantity(14)
                    .vendorId("user-id")
                    .images(List.of("product-image"))
                    .description("Testing update product")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        @Test
        void updateProduct_PartialUpdateOfProduct() {
            when(productRepository.findById("product-id")).thenReturn(Optional.of(product));

            ProductDto request = ProductDto.builder()
                    .name("update test product")
                    .price(10.2)
                    .description("Updated the test product")
                    .build();

            ProductDto response = underTest.updateProduct("product-id", request, headers);

            assertEquals(product.getId(), response.getId());
            assertEquals(product.getCategory(), response.getCategory());
            assertEquals(product.getQuantity(), response.getQuantity());
            assertEquals(product.getVendorId(), response.getVendor_id());
            assertEquals(product.getImages(), response.getImages());

            assertEquals(request.getName(), response.getName());
            assertEquals(request.getPrice(), response.getPrice());
            assertEquals(request.getDescription(), response.getDescription());

            verify(productRepository).save(any(Product.class));

        }
    }

    @Nested
    class UpdateInventoryTests {

        @Test
        void updateInventory() {
        }
    }

    @Nested
    class DeleteProductTests {

        @Test
        void deleteProduct() {
        }
    }



}