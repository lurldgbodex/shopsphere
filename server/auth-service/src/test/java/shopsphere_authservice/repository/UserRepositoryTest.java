package shopsphere_authservice.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import shopsphere_authservice.entity.User;
import shopsphere_authservice.enums.UserRole;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@user.com")
                .password("password")
                .googleId("google-id")
                .role(UserRole.USER)
                .build();

        underTest.save(user);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void existsByEmailTest_whenUserExist() {
        boolean exists = underTest.existsByEmail(user.getEmail());
        assertTrue(exists);
    }

    @Test
    void existsByEmailTest_whenUserDoesNotExist() {
        boolean exists = underTest.existsByEmail("invalid@user.com");
        assertFalse(exists);
    }

    @Test
    void findByEmail_whenEmailIsValid() {
        Optional<User> response = underTest.findByEmail(user.getEmail());

        assertTrue(response.isPresent());
        assertEquals(user, response.get());
    }

    @Test
    void findByEmail_whenEmailIsNotValid() {
        Optional<User> response = underTest.findByEmail("invalid@user.com");

        assertTrue(response.isEmpty());
    }

    @Test
    void findByGoogleId_whenGoogleIdIsValid() {
        Optional<User> response = underTest.findByGoogleId(user.getGoogleId());

        assertTrue(response.isPresent());
        assertEquals(user, response.get());
    }

    @Test
    void findByGoogleId_whenGoogleIdIsNotValid() {
        Optional<User> response = underTest.findByGoogleId("invalid-id");

        assertTrue(response.isEmpty());
    }
}