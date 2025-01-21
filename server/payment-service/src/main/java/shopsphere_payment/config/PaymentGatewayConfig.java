package shopsphere_logging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shopsphere_logging.service.StripePaymentGateway;
import shopsphere_logging.service.interfaces.PaymentGateway;

@Configuration
public class PaymentGatewayConfig {

    @Bean
    public PaymentGateway paymentGateway() {
        return new StripePaymentGateway();
    }
}
