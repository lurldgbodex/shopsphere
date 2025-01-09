package shopsphere_authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shopsphere_authservice.entity.RefreshToken;
import shopsphere_authservice.repository.RefreshTokenRepository;
import shopsphere_authservice.utils.JwtUtils;
import shopsphere_shared.exceptions.UnauthorizedException;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtUtils jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(String email) {
        long refreshTokenDurationMs = TimeUnit.DAYS.toMillis(7);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(jwtUtil.generateRefreshToken(email));
        refreshToken.setEmail(email);
        refreshToken.setExpiryDate(Instant.now()
                .plusMillis(refreshTokenDurationMs));

        refreshTokenRepository.deleteByEmail(email);
        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }

    public void validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("refresh token expired");
        }
    }
}
