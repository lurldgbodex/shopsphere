package shopsphere_gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import shopsphere_gateway.dto.JwtPayload;
import shopsphere_gateway.utils.JwtUtil;

import java.nio.charset.StandardCharsets;

@Slf4j
@Order(-1)
@Component
@RequiredArgsConstructor
public class JwtGlobalFilter implements GlobalFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    private final JwtUtil jwtUtil;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getURI().getPath();

        log.info("JwtGlobalFilter called for path: {} ", requestPath);
        if (requestPath.startsWith("/api/v1/auth")) {
            log.info("Skipping filter because it's unprotected route: {}", requestPath);
            return chain.filter(exchange);
        }

        String authHeaders = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || !authHeaders.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header");
            return unauthorizedResponse(exchange, "Missing or invalid token");
        }

        String token = authHeaders.substring(BEARER_PREFIX.length());
        try {
            JwtPayload payload = jwtUtil.getPayload(token);
            log.info("Payload extracted: userId={}, email={}, role={}",
                    payload.userId(), payload.email(), payload.role());

            if (payload.userId() == null || payload.email() == null || payload.role() == null) {
                log.warn("Invalid payload: Missing required field");
                return unauthorizedResponse(exchange, "Invalid token payload");
            }

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(USER_ID_HEADER, payload.userId())
                    .header(USER_EMAIL_HEADER, payload.email())
                    .header(USER_ROLE_HEADER, payload.role())
                    .build();

            log.info("Request modified with user details");
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception ex) {
            log.error("JWT validation failed: {}", ex.getMessage());
            return unauthorizedResponse(exchange, "Invalid or expired token");
        }
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String errorMessage) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.WWW_AUTHENTICATE,
                "Bearer realm=\"Access to protected resource\"");
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);


        String errorResponse = String
                .format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", errorMessage);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
