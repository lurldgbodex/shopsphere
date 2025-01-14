package shopsphere_order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopsphere_order.dto.request.CreateOrder;
import shopsphere_order.dto.response.ItemResponse;
import shopsphere_order.dto.response.OrderResponse;
import shopsphere_order.entity.Order;
import shopsphere_order.entity.OrderItem;
import shopsphere_order.enums.OrderStatus;
import shopsphere_order.repository.OrderRepository;
import shopsphere_shared.dto.HeaderPayload;
import shopsphere_shared.exceptions.ForbiddenException;
import shopsphere_shared.exceptions.NotFoundException;
import shopsphere_shared.utils.HeaderUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderResponse createOrder(CreateOrder request, HttpHeaders headers) {
        String userId = HeaderUtil.payload(headers).userId();

        Order order = new Order();
        order.setUserId(userId);
        order.setVendorId(request.vendor_id());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = request.items().stream()
                .map(itemRequest -> {
                    BigDecimal totalPrice = itemRequest.getPrice_per_unit()
                            .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                    return OrderItem.builder()
                            .productId(itemRequest.getProduct_id())
                            .quantity(itemRequest.getQuantity())
                            .pricePerUnit(itemRequest.getPrice_per_unit())
                            .totalPrice(totalPrice)
                            .order(order)
                            .build();
                }).toList();

        order.setItems(items);
        order.setTotalPrice(items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        orderRepository.saveAndFlush(order);

        return mapToOrderResponse(order);
    }

    public List<OrderResponse> getOrderByUser(HttpHeaders headers) {
        String userId = HeaderUtil.payload(headers).userId();
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    public OrderResponse getOrderByID(UUID orderID, HttpHeaders headers) {
        HeaderPayload payload = HeaderUtil.payload(headers);
        String userId = payload.userId();
        String userRole = payload.role();

        Order order = findById(orderID);
        if (!userRole.equalsIgnoreCase("admin") &&
                !order.getUserId().equals(userId) && !order.getVendorId().equalsIgnoreCase(userId)) {
            throw new ForbiddenException("you are not authorized to perform action");
        }

        return mapToOrderResponse(order);
    }

    public void updateOrderStatus(UUID orderId, OrderStatus status, HttpHeaders headers) {
        HeaderPayload payload = HeaderUtil.payload(headers);
        String userId = payload.userId();
        String userRole = payload.role();

        if (!userRole.equalsIgnoreCase("admin")
                && !userRole.equalsIgnoreCase("vendor")) {
            throw new ForbiddenException("you are not authorized to perform action");
        }

        Order order = findById(orderId);

        if (userRole.equalsIgnoreCase("vendor")
                && !order.getVendorId().equalsIgnoreCase(userId)) {
            throw  new ForbiddenException("you are not authorized to perform action");
        }

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private Order findById(UUID orderID) {
        return orderRepository.findById(orderID)
                .orElseThrow(() -> new NotFoundException("order not found"));
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<ItemResponse> items = order.getItems().stream()
                .map(item -> ItemResponse.builder()
                        .product_id(item.getProductId())
                        .quantity(item.getQuantity())
                        .price_per_unit(item.getPricePerUnit())
                        .total_price(item.getTotalPrice())
                        .build()).toList();

        return OrderResponse.builder()
                .id(order.getId())
                .user_id(order.getUserId())
                .status(order.getStatus())
                .total_price(order.getTotalPrice())
                .created_at(order.getCreatedAt())
                .updated_at(order.getUpdatedAt())
                .items(items)
                .build();
    }
}
