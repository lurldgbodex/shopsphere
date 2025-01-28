package shopsphere_gateway.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import shopsphere_gateway.dto.JwtPayload;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class JwtUtilTest {
    @Mock
    private Claims claims;
    @Mock
    private Jws<Claims> jws;
    @Mock
    private JwtParserBuilder parserBuilder;
    @Mock
    private JwtParser parser;
    @InjectMocks
    private JwtUtil jwtUtil;

    private static final String JWT_SECRET = "my-secret-key-1234567890-1234567890-1234567890";
    private static final String VALID_TOKEN = "valid.token.here";
    private static final String INVALID_TOKEN = "invalid.token.here";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", JWT_SECRET);
    }

    @Test
    void testGetPayload_validToken() {
        try (MockedStatic<Jwts> jwtMock = mockStatic(Jwts.class)) {

            jwtMock.when(Jwts::parser).thenReturn(parserBuilder);
            when(parserBuilder.verifyWith(any(SecretKey.class))).thenReturn(parserBuilder);
            when(parserBuilder.build()).thenReturn(parser);
            when(parser.parseSignedClaims(VALID_TOKEN)).thenReturn(jws);
            when(jws.getPayload()).thenReturn(claims);

            when(claims.getSubject()).thenReturn("user123");
            when(claims.get("email", String.class)).thenReturn("user@example.com");
            when(claims.get("role", String.class)).thenReturn("ROLE_USER");

            JwtPayload payload = jwtUtil.getPayload(VALID_TOKEN);

            assertEquals("user123", payload.userId());
            assertEquals("user@example.com", payload.email());
            assertEquals("ROLE_USER", payload.role());
        }
    }

    @Test
    void testGetPayload_InvalidToken() {
        try (MockedStatic<Jwts> jwtMock = mockStatic(Jwts.class)) {
            jwtMock.when(Jwts::parser).thenReturn(parserBuilder);

            when(parserBuilder.verifyWith(any(SecretKey.class))).thenReturn(parserBuilder);
            when(parserBuilder.build()).thenReturn(parser);
            when(parser.parseSignedClaims(anyString()))
                    .thenThrow(new RuntimeException("Invalid token"));

            assertThrows(RuntimeException.class, () -> jwtUtil.getPayload(INVALID_TOKEN));
        }
    }

    @Test
    void testGetPayload_MissingClaims() {
        try (MockedStatic<Jwts> jwtMock = mockStatic(Jwts.class)) {
            jwtMock.when(Jwts::parser).thenReturn(parserBuilder);

            when(parserBuilder.verifyWith(any(SecretKey.class))).thenReturn(parserBuilder);
            when(parserBuilder.build()).thenReturn(parser);
            when(parser.parseSignedClaims(anyString())).thenReturn(jws);
            when(jws.getPayload()).thenReturn(claims);

            when(claims.getSubject()).thenReturn(null);
            when(claims.get("email", String.class)).thenReturn(null);
            when(claims.get("role", String.class)).thenReturn(null);

            JwtPayload payload = jwtUtil.getPayload(VALID_TOKEN);

            assertNull(payload.role());
            assertNull(payload.email());
            assertNull(payload.userId());
        }
    }
}