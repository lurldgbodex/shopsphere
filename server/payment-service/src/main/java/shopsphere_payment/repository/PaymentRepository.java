package shopsphere_logging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shopsphere_logging.entity.Payment;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
