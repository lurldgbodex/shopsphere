package shopsphere_order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import shopsphere_order.dto.request.CreateOrder;
import shopsphere_order.dto.request.ItemRequest;
import shopsphere_order.dto.response.OrderResponse;
import shopsphere_order.entity.Order;
import shopsphere_order.entity.OrderItem;
import shopsphere_order.enums.OrderStatus;
import shopsphere_order.repository.OrderRepository;
import shopsphere_shared.exceptions.ForbiddenException;
import shopsphere_shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @InjectMocks private  OrderService underTest;
    private HttpHeaders headers;
    private Order order;

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.set("X-User-Id", "user-id");
        headers.set("X-User-Role", "admin");

        order = Order.builder()
                .id(UUID.randomUUID())
                .userId("user-id")
                .vendorId("vendor-id")
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .totalPrice(BigDecimal.valueOf(100))
                .items(Collections.singletonList(new OrderItem()))
                .build();
    }

    @Nested
    class CreateOrderTest {

        @Test
        void testCreateOrder() {
            ItemRequest request = ItemRequest.builder()
                    .product_id("product-id")
                    .quantity(5)
                    .price_per_unit(BigDecimal.valueOf(20))
                    .build();

            CreateOrder orderRequest = CreateOrder.builder()
                    .items(Collections.singletonList(request))
                    .build();

            when(orderRepository.saveAndFlush(any(Order.class)))
                    .thenReturn(order);

            OrderResponse response = underTest.createOrder(orderRequest, headers);

            assertNotNull(response);
            assertEquals(order.getUserId(), response.user_id());
            assertEquals(order.getStatus(), response.status());
            assertEquals(order.getTotalPrice(), response.total_price());

            verify(orderRepository).saveAndFlush(any(Order.class));
        }
    }

    @Nested
    class GetOrderTest {

        @Test
        void testGetOrderByUser() {
            when(orderRepository.findByUserId("user-id")).thenReturn(List.of(order));

            List<OrderResponse> response = underTest.getOrderByUser( headers);

            assertFalse(response.isEmpty());
            assertEquals(response.get(0).user_id(), order.getUserId());
            verify(orderRepository).findByUserId("user-id");
        }

        @Test
        void testGetOrderByID_orderNotFound() {
            UUID orderId = UUID.randomUUID();
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            Exception ex = assertThrows(NotFoundException.class, () ->
                    underTest.getOrderByID(orderId, headers));

            assertEquals("order not found", ex.getMessage());
            verify(orderRepository).findById(orderId);
        }

        @Test
        void testGetOrderByID_adminUserRole() {
            UUID orderId = order.getId();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            OrderResponse response = underTest.getOrderByID(orderId, headers);

            assertNotNull(response);
            assertEquals(order.getId(), response.id());
            assertEquals(order.getStatus(), response.status());
            assertEquals(order.getUserId(), response.user_id());
            assertEquals(order.getTotalPrice(), response.total_price());
            assertEquals(order.getCreatedAt(), response.created_at());

            verify(orderRepository).findById(orderId);
        }

        @Test
        void testGetOrderById_userRole() {
            headers = new HttpHeaders();
            headers.set("X-User-Id", "user-id");
            headers.set("X-User-Role", "vendor");

            UUID orderId = order.getId();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            OrderResponse response = underTest.getOrderByID(orderId, headers);

            assertNotNull(response);
            assertEquals(order.getId(), response.id());
            assertEquals(order.getStatus(), response.status());
            assertEquals(order.getUserId(), response.user_id());
            assertEquals(order.getTotalPrice(), response.total_price());
            assertEquals(order.getCreatedAt(), response.created_at());

            verify(orderRepository).findById(orderId);
        }

        @Test
        void testGetOrderById_vendorRole() {
            headers = new HttpHeaders();
            headers.set("X-User-Id", "vendor-id");
            headers.set("X-User-Role", "vendor");

            UUID orderId = order.getId();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            OrderResponse response = underTest.getOrderByID(orderId, headers);

            assertNotNull(response);
            assertEquals(order.getId(), response.id());
            assertEquals(order.getStatus(), response.status());
            assertEquals(order.getUserId(), response.user_id());
            assertEquals(order.getTotalPrice(), response.total_price());
            assertEquals(order.getCreatedAt(), response.created_at());

            verify(orderRepository).findById(orderId);
        }

        @Test
        void testGetOrderById_vendorNotOwner() {
            headers = new HttpHeaders();
            headers.set("X-User-Id", "another-vendor");
            headers.set("X-User-Role", "vendor");

            UUID orderId = order.getId();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            Exception ex = assertThrows(ForbiddenException.class,
                    () -> underTest.getOrderByID(orderId, headers));

            assertEquals("you are not authorized to perform action", ex.getMessage());
        }

        @Test
        void testGetOrderById_userNotOwner() {
            headers = new HttpHeaders();
            headers.set("X-User-Id", "another-user");
            headers.set("X-User-Role", "user");

            UUID orderId = order.getId();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            Exception ex = assertThrows(ForbiddenException.class,
                    () -> underTest.getOrderByID(orderId, headers));

            assertEquals("you are not authorized to perform action", ex.getMessage());
        }
    }

    @Nested
    class UpdateOrderTest {

        @Test
        void testUpdateOrderStatus() {
            UUID orderId = order.getId();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            underTest.updateOrderStatus(orderId, OrderStatus.DELIVERED, headers);

            assertEquals(order.getStatus(), OrderStatus.DELIVERED);

            verify(orderRepository).findById(orderId);
            verify(orderRepository).save(order);
        }

        @Test
        void testUpdateOrderStatus_userIsVendor() {
            headers = new HttpHeaders();
            headers.set("X-User-Id", "vendor-id");
            headers.set("X-User-Role", "vendor");

            UUID orderId = order.getId();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            underTest.updateOrderStatus(orderId, OrderStatus.DELIVERED, headers);

            assertEquals(order.getStatus(), OrderStatus.DELIVERED);

            verify(orderRepository).findById(orderId);
            verify(orderRepository).save(order);
        }

        @Test
        void testUpdateOrderStatus_differentVendor() {
            headers = new HttpHeaders();
            headers.set("X-User-Id", "a-vendor-id");
            headers.set("X-User-Role", "vendor");

            UUID orderId = order.getId();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            Exception ex = assertThrows(ForbiddenException.class,
                    () -> underTest.updateOrderStatus(orderId, OrderStatus.DELIVERED, headers));

            assertEquals("you are not authorized to perform action", ex.getMessage());
            verify(orderRepository, never()).save(any(Order.class));
        }

        @Test
        void testUpdateOrderStatus_userRole() {
            headers = new HttpHeaders();
            headers.set("X-User-Id", "user-id");
            headers.set("X-User-Role", "user");

            UUID orderId = order.getId();

            Exception ex = assertThrows(ForbiddenException.class,
                    () -> underTest.updateOrderStatus(orderId, OrderStatus.DELIVERED, headers));

            assertEquals("you are not authorized to perform action", ex.getMessage());
            verify(orderRepository, never()).save(any(Order.class));
            verify(orderRepository, never()).findById(any(UUID.class));
        }
    }
}