package shopsphere_productservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shopsphere_productservice.dto.response.PaginatedResponse;
import shopsphere_productservice.dto.response.ProductDto;
import shopsphere_productservice.service.ProductService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    private ProductDto productDto;
    @Autowired private MockMvc mockMvc;
    @MockitoBean private ProductService productService;

    @BeforeEach
    void setUp() {
        productDto = ProductDto.builder()
                .id("product-id")
                .name("test product")
                .price(25.4)
                .quantity(50)
                .category("Test")
                .vendor_id("vendor-id")
                .images(List.of("image-url1", "image-url2"))
                .description("test product description")
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
    }


    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    void getProductById() throws Exception {
        when(productService.getProductById("product-id")).thenReturn(productDto);

        mockMvc.perform(get("/api/v1/products/product-id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(productDto.getId()))
                .andExpect(jsonPath("$.name").value(productDto.getName()))
                .andExpect(jsonPath("$.price").value(productDto.getPrice()))
                .andExpect(jsonPath("$.quantity").value(productDto.getQuantity()))
                .andExpect(jsonPath("$.category").value(productDto.getCategory()))
                .andExpect(jsonPath("$.description").value(productDto.getDescription()));

    }

    @Test
    void getProducts() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDto> pageDto = new PageImpl<>(List.of(productDto));
        PaginatedResponse<ProductDto> response = new PaginatedResponse<>(pageDto);

        when(productService.getProducts(null, pageable)).thenReturn(response);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page").value(response.getPage()))
                .andExpect(jsonPath("$.size").value(response.getSize()))
                .andExpect(jsonPath("$.content").isArray());
    }
}