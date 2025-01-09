package shopsphere_authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shopsphere_authservice.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByEmail(String email);
}
