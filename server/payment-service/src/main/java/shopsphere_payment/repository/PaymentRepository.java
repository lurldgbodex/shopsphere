package shopsphere_payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shopsphere_payment.entity.Payment;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
