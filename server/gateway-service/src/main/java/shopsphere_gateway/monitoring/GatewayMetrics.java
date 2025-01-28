package shopsphere_gateway.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class GatewayMetrics {
    private final Counter authenticationFailures;

    public GatewayMetrics(MeterRegistry registry) {
        this.authenticationFailures = Counter.builder("gateway.authentication.failures")
                .description("Number of authentication failures")
                .register(registry);
    }

    public void incrementAuthenticationFailures() {
        authenticationFailures.increment();
    }
}
