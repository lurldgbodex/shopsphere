package shopsphere_gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtRequestFilter {

    public Mono<Void> addJwtHeaders(ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth instanceof Jwt)
                .map(auth -> (Jwt) auth.getPrincipal())
                .flatMap(jwt -> {
                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("X-User-Id", jwt.getClaim("user_id"))
                            .header("X-User-Email", jwt.getSubject())
                            .header("X-User-Role", jwt.getClaim("role"))
                            .build();

                    exchange.mutate().request(request).build();
                    return Mono.empty();
                });
    }
}
