package shopsphere_payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpHeaders;
import shopsphere_payment.dto.request.PaymentGatewayRequest;
import shopsphere_payment.dto.request.PaymentRequest;
import shopsphere_payment.dto.response.PaymentGatewayResponse;
import shopsphere_payment.dto.response.PaymentResponse;
import shopsphere_payment.entity.Payment;
import shopsphere_payment.enums.PaymentStatus;
import shopsphere_payment.repository.PaymentRepository;
import shopsphere_payment.service.interfaces.PaymentGateway;
import shopsphere.shared.exceptions.ForbiddenException;
import shopsphere.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private HttpHeaders headers;
    @Mock private PaymentGateway paymentGateway;
    @Mock private RabbitTemplate rabbitTemplate;
    @InjectMocks private PaymentService underTest;
    @Mock private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.set("X-User-Id", "user-id");
        headers.set("X-User-Role", "admin");
    }

    @Test
    void processPayment() {
        PaymentRequest request = PaymentRequest.builder()
                .amount(BigDecimal.valueOf(10))
                .currency("NGN")
                .paymentMethod("card")
                .order_id("order-id")
                .build();

        Payment savedPayment = Payment.builder()
                .id(UUID.randomUUID())
                .userId("user-id")
                .orderId("order-id")
                .amount(BigDecimal.valueOf(10))
                .paymentMethod("card")
                .status(PaymentStatus.PENDING)
                .build();

        PaymentGatewayResponse gatewayResponse = PaymentGatewayResponse.builder()
                .gateway_message("succeeded")
                .status("succeeded")
                .transaction_id("transaction-id")
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(paymentGateway.initiatePayment(any(PaymentGatewayRequest.class))).thenReturn(gatewayResponse);

        PaymentResponse response = underTest.processPayment(request, headers);

        assertNotNull(response);
        assertEquals("PENDING", response.status().toString());
        assertEquals("succeeded", response.message());

        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(paymentGateway).initiatePayment(any(PaymentGatewayRequest.class));
        verify(rabbitTemplate).convertAndSend(eq("payment.exchange"),
                eq("payment.created"), any(Payment.class));
    }

    @Test
    void getPaymentById() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setOrderId("order-123");
        payment.setUserId("user-123");
        payment.setStatus(PaymentStatus.FAILED);
        payment.setAmount(BigDecimal.valueOf(50));
        payment.setPaymentMethod("card");

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        PaymentResponse response = underTest.getPaymentById(paymentId, headers);

        assertNotNull(response);
        assertEquals(paymentId, response.payment_id());
        assertEquals("FAILED", response.status().toString());
        assertEquals("payment details fetched successfully", response.message());
    }

    @Test
    void getPaymentById_whenRoleUser() {
        headers = new HttpHeaders();
        headers.set("X-User-Role", "user");

        Exception ex = assertThrows( ForbiddenException.class,
                () -> underTest.getPaymentById(UUID.randomUUID(), headers));

        assertEquals("Access Denied", ex.getMessage());
    }

    @Test
    void getPaymentById_invalidPaymentId() {
        when(paymentRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class,
                () -> underTest.getPaymentById(UUID.randomUUID(), headers));

        assertEquals("payment not found", ex.getMessage());
    }
}