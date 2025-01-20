package shopsphere_logging.service.interfaces;

import shopsphere_logging.dto.request.PaymentGatewayRequest;
import shopsphere_logging.dto.response.PaymentGatewayResponse;

public interface PaymentGateway {
    PaymentGatewayResponse initiatePayment(PaymentGatewayRequest request);
    PaymentGatewayResponse verifyPayment(String transactionId);
}
