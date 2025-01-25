package shopsphere_gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import shopsphere_gateway.dto.JwtPayload;
import shopsphere_gateway.utils.JwtUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGlobalFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("** JwtGlobalFilter called **");
        ServerHttpRequest request = exchange.getRequest();

        log.info("Request URI: " + request.getURI().getPath());
        if (request.getURI().getPath().startsWith("/api/v1/auth")) {
            log.info("** Skipping filter because it's unprotected route **");
            return chain.filter(exchange);
        }

        String authHeaders = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || !authHeaders.startsWith("Bearer ")) {
            log.info("** Invalid or Empty token - Does not start with Bearer **");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            log.info("** Extracting Token From Header **");
            String token = authHeaders.substring(7);

            log.info("** Extracting payload from Token **");
            JwtPayload payload = jwtUtil.getPayload(token);
            log.info("** Payload extracted **");

            log.info("** Modifying Request **");
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", payload.userId())
                    .header("X-User-Email", payload.email())
                    .header("X-User-Role", payload.role())
                    .build();

            log.info("** Request Modified **");
            exchange = exchange.mutate().request(modifiedRequest).build();
            return chain.filter(exchange);
        } catch (Exception ex) {
            log.error("Invalid JWT Token: " + ex.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
