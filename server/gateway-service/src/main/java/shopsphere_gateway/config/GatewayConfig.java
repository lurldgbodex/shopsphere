package shopsphere_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shopsphere_gateway.filter.JwtRequestFilter;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .uri("http://localhost:8181"))
                .route("order-service", r -> r.path("/api/v1/orders/**")
                        .filters(f -> f.filter(((exchange, chain) ->
                                jwtRequestFilter.addJwtHeaders(exchange).then(chain.filter(exchange)))))
                        .uri("http://localhost:8282"))
                .route("payment-service", r -> r.path("/api/v1/payments/**")
                        .filters(f -> f.filter(((exchange, chain) ->
                                jwtRequestFilter.addJwtHeaders(exchange).then(chain.filter(exchange)))))
                        .uri("http://localhost:8383"))
                .route("product-service", r -> r.path("/api/v1/products/**")
                        .filters(f -> f.filter((((exchange, chain) ->
                                jwtRequestFilter.addJwtHeaders(exchange).then(chain.filter(exchange))))))
                        .uri("http://localhost:8484"))
                .build();
    }
}
