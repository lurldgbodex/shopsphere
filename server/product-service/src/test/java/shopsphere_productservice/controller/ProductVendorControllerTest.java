package shopsphere_productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shopsphere_productservice.dto.request.CreateRequest;
import shopsphere_productservice.dto.response.ProductDto;
import shopsphere_productservice.service.VendorProductService;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ProductVendorController.class)
class ProductVendorControllerTest {

    private HttpHeaders headers;
    private ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;
    @MockitoBean private VendorProductService vendorService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        headers = new HttpHeaders();
        headers.set("X-User-Role", "vendor");
        headers.set("X-User-Id", "user-id");
    }

    @Nested
    class CreateProductTest {
        private CreateRequest request;

        @BeforeEach
        void setUpCreateProduct() {
            request = CreateRequest.builder()
                    .name("Product Test")
                    .price(5.0)
                    .quantity(2)
                    .category("test")
                    .images(List.of("product-image"))
                    .description("Testing Product Creation")
                    .build();
        }

        @Test
        void createProduct_success() throws Exception {
            String requestString = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/api/v1/vendor/products")
                            .headers(headers)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isCreated());
        }

        @Test
        void createProduct_missingRequiredData() throws Exception {
            request = new CreateRequest();
            String requestString = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/api/v1/vendor/products")
                            .headers(headers)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors.name").value("name is required"))
                    .andExpect(jsonPath("$.errors.category").value("category is required"))
                    .andExpect(jsonPath("$.errors.images").value("images is required"))
                    .andExpect(jsonPath("$.errors.description").value("description is required"));
        }

        @Test
        void createProduct_invalidQuantity() throws Exception {
            request.setQuantity(0);
            String requestString = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/api/v1/vendor/products")
                            .headers(headers)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors.quantity").value("quantity must be more than 0"));
        }
    }

    @Test
    void updateProductTest() throws Exception {
        ProductDto request = ProductDto.builder()
                .name("update request")
                .build();

        String requestString = objectMapper.writeValueAsString(request);
        when(vendorService.updateProduct("product-id", request, headers))
                .thenReturn(request);

        mockMvc.perform(put("/api/v1/vendor/products/product-id")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct() throws Exception {
        doNothing().when(vendorService).deleteProduct("product-id", headers);

        mockMvc.perform(delete("/api/v1/vendor/products/product-id")
                        .headers(headers))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateInventory() throws Exception {
        doNothing().when(vendorService).updateInventory("product-id", 5);

        mockMvc.perform(patch("/api/v1/vendor/products/product-id/inventory?quantityDelta=5")
                        .headers(headers))
                .andExpect(status().isNoContent());
    }
}