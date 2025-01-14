package shopsphere_order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopsphere_order.dto.request.CreateOrder;
import shopsphere_order.dto.request.UpdateRequest;
import shopsphere_order.dto.response.OrderResponse;
import shopsphere_order.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrder request,
                                                     @RequestHeader HttpHeaders headers) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request, headers));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderByUser(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(orderService.getOrderByUser(headers));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId,
                                                      @RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(orderService.getOrderByID(orderId, headers));
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable UUID orderId,
                                                  @RequestBody UpdateRequest status,
                                                  @RequestHeader HttpHeaders header) {
        orderService.updateOrderStatus(orderId, status.getStatus(), header);
        return ResponseEntity.noContent().build();
    }
}
