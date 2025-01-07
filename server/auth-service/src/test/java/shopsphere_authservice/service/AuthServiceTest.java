package shopsphere_authservice.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import shopsphere_authservice.dto.request.GoogleLoginRequest;
import shopsphere_authservice.dto.request.LoginRequest;
import shopsphere_authservice.dto.request.RegisterRequest;
import shopsphere_authservice.dto.response.UserResponse;
import shopsphere_authservice.entity.User;
import shopsphere_authservice.enums.UserRole;
import shopsphere_authservice.repository.UserRepository;
import shopsphere_authservice.utils.JwtUtils;
import shopsphere_shared.exceptions.ConflictException;
import shopsphere_shared.exceptions.UnauthorizedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks private AuthService underTest;
    @Mock private UserRepository userRepository;
    @Mock private JwtUtils jwtUtils;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private GoogleAuthService googleAuthService;

    @Nested
    class RegisterUserTests {

        @Test
        void shouldRegisterUser() {
            RegisterRequest request = new RegisterRequest("test@user.com", "password");
            when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");

            when(jwtUtils.generateToken(eq("test@user.com"), anyMap())).thenReturn("dummy-token");

            when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                return User.builder()
                        .id(1L)
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .role(user.getRole())
                        .build();
            });

            UserResponse response = underTest.register(request, UserRole.USER);
            ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

            verify(userRepository).saveAndFlush(userArgumentCaptor.capture());
            User capturedUser = userArgumentCaptor.getValue();

            assertEquals("test@user.com", capturedUser.getEmail());
            assertEquals("encoded-password", capturedUser.getPassword());
            assertEquals("USER", capturedUser.getRole().toString());

            assertNotNull(response.access_token());
            assertEquals("dummy-token", response.access_token());
        }


        @Test
        void registerUser_withAlreadyExistingDetails() {
            RegisterRequest request = new RegisterRequest("test@user.com", "password");

            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            Exception ex = assertThrows(ConflictException.class, () ->
                    underTest.register(request, UserRole.USER));

            assertEquals("User already exists", ex.getMessage());
            verify(userRepository).existsByEmail(request.email());
            verify(userRepository, never()).saveAndFlush(any(User.class));
        }
    }

    @Nested
    class LoginUserTest{

        @Test
        void shouldLoginUser() {
            User user = User.builder()
                    .id(55L)
                    .email("test@user.email")
                    .password("encoded-password")
                    .role(UserRole.VENDOR)
                    .build();

            LoginRequest request = LoginRequest.builder()
                    .email("test@user.com")
                    .password("password")
                    .build();

            String token = "dummy-token";
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole());

            when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
            when(jwtUtils.generateToken(user.getEmail(), claims)).thenReturn(token);

            UserResponse response = underTest.login(request);

            assertEquals(response.access_token(), token);
            verify(userRepository).findByEmail(request.email());
            verify(passwordEncoder).matches(anyString(), anyString());
            verify(jwtUtils).generateToken(anyString(), anyMap());
        }

        @Test
        void loginUser_withWrongPassword() {
            User user = User.builder()
                    .id(55L)
                    .email("test@user.email")
                    .password("encoded-password")
                    .role(UserRole.VENDOR)
                    .build();

            LoginRequest request = LoginRequest.builder()
                    .email("test@user.com")
                    .password("password")
                    .build();

            when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

            Exception ex = assertThrows(UnauthorizedException.class, () ->
                    underTest.login(request));

            assertEquals("invalid login credentials", ex.getMessage());
            verify(userRepository).findByEmail(request.email());
            verify(passwordEncoder).matches(anyString(), anyString());
            verify(jwtUtils, never()).generateToken(anyString(), anyMap());
        }

        @Test
        void loginUser_withInvalidEmail() {
            LoginRequest request = LoginRequest.builder()
                    .email("test@user.com")
                    .password("password")
                    .build();

            when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

            Exception ex = assertThrows(UnauthorizedException.class, () ->
                    underTest.login(request));

            assertEquals("invalid login credentials", ex.getMessage());
            verify(userRepository).findByEmail(request.email());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtUtils, never()).generateToken(anyString(), anyMap());
        }
    }

    @Nested
    class GoogleLoginTest {

        @Test
        void googleLoginSuccess_whenUserDoesNotExist() {
            String googleId = "google-id";

            GoogleLoginRequest request = new GoogleLoginRequest();
            request.setIdToken("idToken");

            when(googleAuthService.verifyGoogleToken("idToken")).thenReturn(googleId);
            when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.empty());
            when(jwtUtils.generateToken(anyString(), anyMap())).thenReturn("dummy-token");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                return User.builder()
                        .id(4L)
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .role(UserRole.ADMIN)
                        .build();
            });

            UserResponse response = underTest.googleLogin(request, UserRole.ADMIN);
            ArgumentCaptor<User> googleLoginCaptor = ArgumentCaptor.forClass(User.class);

            verify(userRepository).save(googleLoginCaptor.capture());
            User capturedUser = googleLoginCaptor.getValue();

            assertEquals(googleId, capturedUser.getGoogleId());
            assertEquals("google_" + googleId, capturedUser.getEmail());
            assertEquals("ADMIN", capturedUser.getRole().toString());

            assertEquals("dummy-token", response.access_token());
            verify(userRepository).findByGoogleId(googleId);
            verify(jwtUtils).generateToken(anyString(), anyMap());
        }

        @Test
        void googleLogin_whenUserExist() {
            GoogleLoginRequest request = new GoogleLoginRequest();
            request.setIdToken("idToken");

            String googleId = "google-id";
            User user = User.builder()
                    .id(10L)
                    .googleId("google-id")
                    .email("email@google.com")
                    .role(UserRole.VENDOR)
                    .build();

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole());

            when(googleAuthService.verifyGoogleToken(request.getIdToken())).thenReturn(googleId);
            when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.of(user));
            when(jwtUtils.generateToken(user.getEmail(), claims)).thenReturn("dummy-token");

            UserResponse response = underTest.googleLogin(request, UserRole.VENDOR);

            assertEquals("dummy-token", response.access_token());
            verify(userRepository, never()).save(any(User.class));
        }
    }
}