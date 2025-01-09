package shopsphere_authservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shopsphere_authservice.entity.RefreshToken;
import shopsphere_authservice.repository.RefreshTokenRepository;
import shopsphere_authservice.utils.JwtUtils;
import shopsphere_shared.exceptions.UnauthorizedException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock private JwtUtils jwtUtil;
    @Mock private RefreshTokenRepository refreshRepository;
    @InjectMocks private RefreshTokenService underTest;

    @Test
    void createRefreshTokenTest() {
        when(jwtUtil.generateRefreshToken("test@user.com"))
                .thenReturn("dummy-refresh-token");
        when(refreshRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);

            return RefreshToken.builder()
                    .id(5L)
                    .token(token.getToken())
                    .email(token.getEmail())
                    .expiryDate(token.getExpiryDate())
                    .build();
        });

        String response = underTest.createRefreshToken("test@user.com");
        ArgumentCaptor<RefreshToken> argumentCaptor = ArgumentCaptor.forClass(RefreshToken.class);

        verify(refreshRepository).save(argumentCaptor.capture());
        RefreshToken capturedEntry = argumentCaptor.getValue();

        assertEquals("test@user.com", capturedEntry.getEmail());
        assertEquals("dummy-refresh-token", capturedEntry.getToken());
        assertNotNull(capturedEntry.getExpiryDate());

        assertEquals("dummy-refresh-token", response);

        verify(jwtUtil).generateRefreshToken(anyString());
        verify(refreshRepository).save(any(RefreshToken.class));
        verify(refreshRepository).deleteByEmail(anyString());
    }

    @Test
    void validateRefreshToken_success() {
        RefreshToken token = RefreshToken.builder()
                .id(5L)
                .token("dummy-token")
                .email("test@user.com")
                .expiryDate(Instant.now().plusSeconds(1000))
                .build();

        when(refreshRepository.findByToken(anyString())).thenReturn(Optional.of(token));

        assertDoesNotThrow(()-> underTest.validateRefreshToken(token.getToken()));
        verify(refreshRepository).findByToken(anyString());
    }

    @Test
    void validateRefreshToken_invalidToken() {
        when(refreshRepository.findByToken(anyString())).thenReturn(Optional.empty());

        Exception ex =  assertThrows(UnauthorizedException.class,
                () -> underTest.validateRefreshToken("dummy-token"));

        assertEquals("invalid refresh token", ex.getMessage());
        verify(refreshRepository).findByToken(anyString());
    }

    @Test
    void validateRefreshToken_expiredToken() {
        RefreshToken token = RefreshToken.builder()
                .id(5L)
                .token("dummy-token")
                .email("test@user.com")
                .expiryDate(Instant.now().minusSeconds(1000))
                .build();

        when(refreshRepository.findByToken(anyString())).thenReturn(Optional.of(token));
        Exception ex =  assertThrows(UnauthorizedException.class,
                () -> underTest.validateRefreshToken("dummy-token"));

        assertEquals("refresh token expired", ex.getMessage());
        verify(refreshRepository).findByToken(anyString());
    }
}