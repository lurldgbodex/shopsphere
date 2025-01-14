package shopsphere_order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shopsphere_order.entity.Order;
import shopsphere_order.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(String userId);
    List<Order> findByUserIdAndStatus(String userId, OrderStatus status);
}
