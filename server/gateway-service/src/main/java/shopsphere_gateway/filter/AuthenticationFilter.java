package shopsphere_gateway.filter;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Order(0)
@Component
public class AuthenticationFilter implements GlobalFilter {

    @Value("${gateway.unprotected-paths}")
    private List<String> unprotectedPaths;
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_EMAIL = "X-User_Email";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (unprotectedPaths.stream().anyMatch(path::equals)) {
            return chain.filter(exchange);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        Map<String, Object> claims = jwtAuthToken.getToken().getClaims();

        String userId = claims.getOrDefault("userId", "").toString();
        String email = claims.getOrDefault("email", "").toString();
        if (userId.isEmpty() || email.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is invalid or missing required claims");
        }

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(HEADER_USER_ID, userId)
                .header(HEADER_USER_EMAIL, email)
                .build();
        exchange = exchange.mutate().request(request).build();

        return chain.filter(exchange);
    }
}
