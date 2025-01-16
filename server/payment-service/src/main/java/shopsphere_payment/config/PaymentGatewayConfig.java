package shopsphere_payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shopsphere_payment.service.StripePaymentGateway;
import shopsphere_payment.service.interfaces.PaymentGateway;

@Configuration
public class PaymentGatewayConfig {

    @Bean
    public PaymentGateway paymentGateway() {
        return new StripePaymentGateway();
    }
}
