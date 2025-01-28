package shopsphere_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = UUID.randomUUID().toString();
        log.info("Request ID: {}, Method: {}, Path: {}, Headers: {}",
                requestId, request.getMethod(), request.getPath(), request.getHeaders());

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-Request-ID", requestId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doOnSuccess((done) -> {
                    log.info("Request ID: {}, Response Status: {}", requestId,
                            exchange.getResponse().getStatusCode());
                })
                .doOnError((error) -> {
                    log.info("Request ID: {}, Error: {}", requestId, error.getMessage());
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
