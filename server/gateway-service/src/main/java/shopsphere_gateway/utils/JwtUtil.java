package shopsphere_gateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shopsphere_gateway.dto.JwtPayload;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;

    public JwtPayload getPayload(String token) {
        Claims claims = extractClaims(token);

        return JwtPayload.builder()
                .email(claims.getSubject())
                .userId(claims.get("userId", String.class))
                .role(claims.get("role", String.class))
                .build();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey generateKey() {
        byte[] key = jwtSecret.getBytes();

        return Keys.hmacShaKeyFor(key);
    }
}
