package shopsphere_gateway.filter;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import shopsphere_gateway.dto.JwtPayload;
import shopsphere_gateway.monitoring.GatewayMetrics;
import shopsphere_gateway.utils.JwtUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtGlobalFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private GatewayFilterChain chain;
    @Mock
    private GatewayMetrics gatewayMetrics;
    @InjectMocks
    private JwtGlobalFilter jwtGlobalFilter;
    @Captor
    private ArgumentCaptor<ServerWebExchange> exchangeCaptor;

    @Test
    void testValidToken() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        JwtPayload payload = JwtPayload.builder()
                .userId("user123")
                .email("user@example.com")
                .role("ROLE_USER")
                .build();

        when(jwtUtil.getPayload("valid-token")).thenReturn(payload);
        when(chain.filter(exchangeCaptor.capture())).thenReturn(Mono.empty());

        jwtGlobalFilter.filter(exchange, chain).block();

        ServerWebExchange capturedExchange = exchangeCaptor.getValue();

        assert capturedExchange.getRequest().getHeaders().containsKey("X-User-Id");
        assert capturedExchange.getRequest().getHeaders().containsKey("X-User-Email");
        assert capturedExchange.getRequest().getHeaders().containsKey("X-User-Role");

        assertThat(capturedExchange.getRequest().getHeaders().getFirst("X-User-Id"))
                .isEqualTo(payload.userId());
        assertThat(capturedExchange.getRequest().getHeaders().getFirst("X-User-Email"))
                .isEqualTo(payload.email());
        assertThat(capturedExchange.getRequest().getHeaders().getFirst("X-User-Role"))
                .isEqualTo(payload.role());
    }

    @Test
    void testMissingToken() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        jwtGlobalFilter.filter(exchange, chain).block();

        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
    }

    @Test
    void testUnprotectedRoute() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/auth/login").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        jwtGlobalFilter.filter(exchange, chain).block();

        // Verify chain is called without modification
        verify(chain, times(1)).filter(exchange);
    }
}