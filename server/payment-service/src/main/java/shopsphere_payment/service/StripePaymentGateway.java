package shopsphere_payment.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shopsphere_payment.dto.request.PaymentGatewayRequest;
import shopsphere_payment.dto.response.PaymentGatewayResponse;
import shopsphere_payment.exception.PaymentException;
import shopsphere_payment.service.interfaces.PaymentGateway;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripePaymentGateway implements PaymentGateway {

    @Value("${payment.stripe.api-key}")
    private String stripeApiKey;

    @Override
    public PaymentGatewayResponse initiatePayment(PaymentGatewayRequest request) {
        Stripe.apiKey = stripeApiKey;

        Map<String, Object> params = new HashMap<>();
        params.put("amount", request.amount()
                .multiply(BigDecimal.valueOf(100))
                .longValue()); // amount in cents
        params.put("currency", request.currency());
        params.put("payment_method_types", request.paymentMethod());
        params.put("description", "payment for Order: " + request.order_id());

        try {
            PaymentIntent intent = PaymentIntent.create(params);

            return mapToPaymentResponse(intent);
        } catch (StripeException ex) {
            throw new PaymentException("Failed to initiate payment: " + ex.getMessage());
        }
    }

    @Override
    public PaymentGatewayResponse verifyPayment(String transactionId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(transactionId);

            return mapToPaymentResponse(intent);
        } catch (StripeException ex) {
            throw new PaymentException("Failed to verify payment: " + ex.getMessage());
        }
    }

    private PaymentGatewayResponse mapToPaymentResponse(PaymentIntent intent) {
        return PaymentGatewayResponse.builder()
                .transaction_id(intent.getId())
                .status(intent.getStatus())
                .gateway_message("Payment initiates successfully")
                .build();
    }
}
