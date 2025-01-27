package shopsphere_authservice.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    private static final long JWT_TOKEN_VALID_TIME = TimeUnit.HOURS.toMillis(1);
    private static final long REFRESH_TOKEN_VALID_TIME = TimeUnit.DAYS.toMillis(7);

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String userId, Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALID_TIME))
                .signWith(generateSigningKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_TIME))
                .signWith(generateSigningKey())
                .compact();
    }

    public String validateToken(String token) {
        return Jwts.parser()
                .verifyWith(generateSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private SecretKey generateSigningKey() {
        byte[] keyInBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyInBytes);
    }
}
