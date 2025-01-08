package shopsphere_authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shopsphere_authservice.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(String googleId);
}
