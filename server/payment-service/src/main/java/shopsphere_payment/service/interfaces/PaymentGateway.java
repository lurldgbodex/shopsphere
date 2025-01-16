package shopsphere_payment.service.interfaces;

import shopsphere_payment.dto.request.PaymentGatewayRequest;
import shopsphere_payment.dto.response.PaymentGatewayResponse;

public interface PaymentGateway {
    PaymentGatewayResponse initiatePayment(PaymentGatewayRequest request);
    PaymentGatewayResponse verifyPayment(String transactionId);
}
