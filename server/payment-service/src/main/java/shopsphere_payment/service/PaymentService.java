package shopsphere_payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import shopsphere_payment.dto.request.PaymentGatewayRequest;
import shopsphere_payment.dto.request.PaymentRequest;
import shopsphere_payment.dto.response.PaymentGatewayResponse;
import shopsphere_payment.dto.response.PaymentResponse;
import shopsphere_payment.entity.Payment;
import shopsphere_payment.enums.PaymentStatus;
import shopsphere_payment.repository.PaymentRepository;
import shopsphere_payment.service.interfaces.PaymentGateway;
import shopsphere_shared.Role;
import shopsphere_shared.exceptions.NotFoundException;
import shopsphere_shared.utils.HeaderUtil;
import shopsphere_shared.utils.RoleUtil;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RabbitTemplate rabbitTemplate;
    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;

    public PaymentResponse processPayment(PaymentRequest request, HttpHeaders headers) {
        String userId = HeaderUtil.payload(headers).userId();
        Payment payment = Payment.builder()
                .userId(userId)
                .orderId(request.order_id())
                .amount(request.amount())
                .paymentMethod(request.paymentMethod())
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        PaymentGatewayRequest gatewayRequest = PaymentGatewayRequest.builder()
                .user_id(userId)
                .order_id(request.order_id())
                .amount(request.amount())
                .currency(request.currency())
                .paymentMethod(request.paymentMethod())
                .build();

        PaymentGatewayResponse gatewayResponse = paymentGateway.initiatePayment(gatewayRequest);

        savedPayment.setStatus(gatewayResponse.status().equals("succeeded")
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED);
        paymentRepository.save(savedPayment);

        rabbitTemplate.convertAndSend("payment.exchange", "payment.created", savedPayment);

        return mapToResponse(payment, gatewayResponse.gateway_message());
    }

    public PaymentResponse getPaymentById(UUID id, HttpHeaders headers) {
        RoleUtil.verifyRole(headers, List.of(Role.ADMIN, Role.VENDOR));
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("payment not found"));

        return mapToResponse(payment, "payment details fetched successfully");
    }

    private PaymentResponse mapToResponse(Payment payment, String message) {
        return PaymentResponse.builder()
                .payment_id(payment.getId())
                .status(payment.getStatus())
                .message(message)
                .build();
    }
}
