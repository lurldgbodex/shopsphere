package shopsphere_order.controller;

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
import shopsphere_order.dto.request.CreateOrder;
import shopsphere_order.dto.request.ItemRequest;
import shopsphere_order.dto.request.UpdateRequest;
import shopsphere_order.dto.response.OrderResponse;
import shopsphere_order.enums.OrderStatus;
import shopsphere_order.service.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    private HttpHeaders headers;
    private ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;
    @MockitoBean private OrderService orderService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        headers = new HttpHeaders();
        headers.set("X-User-Role", "vendor");
        headers.set("X-User-Id", "user-id");
    }

    @Test
    void shouldCreateMockmvc() {
        assertNotNull(mockMvc);
    }

    @Nested
    class CreateOrderTest {

        @Test
        void createOrderTest() throws Exception {
            ItemRequest itemRequest = ItemRequest.builder()
                    .product_id("product-id")
                    .price_per_unit(BigDecimal.valueOf(5))
                    .quantity(5)
                    .build();

            CreateOrder createRequest = CreateOrder.builder()
                    .vendor_id("vendor-id")
                    .items(List.of(itemRequest))
                    .build();

            String requestString = objectMapper.writeValueAsString(createRequest);

            mockMvc.perform(post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isCreated());
        }

        @Test
        void createOrderTest_missingRequiredData() throws Exception {
            CreateOrder createRequest = CreateOrder.builder().build();
            String requestString = objectMapper.writeValueAsString(createRequest);

            mockMvc.perform(post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.vendor_id").value("vendor_id is required"))
                    .andExpect(jsonPath("$.errors.items").value("items is required"));
        }
    }

    @Test
    void getOrderById() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderResponse response = OrderResponse.builder()
                .id(orderId)
                .user_id("user-id")
                .status(OrderStatus.PROCESSING)
                .build();

        when(orderService.getOrderByID(orderId, headers)).thenReturn(response);

        mockMvc.perform(get("/api/v1/orders/" + orderId)
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.user_id").value(response.user_id()));
    }

    @Test
    void getOrderByUser() throws Exception {
        OrderResponse response = OrderResponse.builder()
                .id(UUID.randomUUID())
                .user_id("user-id")
                .status(OrderStatus.PROCESSING)
                .build();

        when(orderService.getOrderByUser(headers)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/orders")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user_id").value(response.user_id()));
    }

    @Nested
    class UpdateOrderStatus {

        @Test
        void updateOrderStatus() throws Exception {
            UUID orderID = UUID.randomUUID();
            UpdateRequest request = new UpdateRequest();
            request.setStatus(OrderStatus.CANCELLED);

            doNothing().when(orderService).updateOrderStatus(orderID, OrderStatus.CANCELLED, headers);
            String requestString = objectMapper.writeValueAsString(request);

            mockMvc.perform(patch("/api/v1/orders/" + orderID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString)
                            .headers(headers))
                    .andExpect(status().isNoContent());
        }

        @Test
        void updateOrderStatus_withoutRequiredData() throws Exception {
            String requestString = "{}";

            UUID orderID = UUID.randomUUID();
            mockMvc.perform(patch("/api/v1/orders/" + orderID)
                            .headers(headers)
                            .content(requestString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

}