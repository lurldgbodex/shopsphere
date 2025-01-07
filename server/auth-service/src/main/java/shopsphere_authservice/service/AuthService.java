package shopsphere_authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtil;
    private final UserRepository userRepository;
    private final GoogleAuthService googleService;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request, UserRole role) {
        validateUserExists(request.email());

        String hashedPassword = passwordEncoder.encode(request.password());
        User newUser = User.builder()
                .email(request.email())
                .password(hashedPassword)
                .role(role)
                .build();

        userRepository.saveAndFlush(newUser);

        return new UserResponse(createToken(newUser));
    }

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("invalid login credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("invalid login credentials");
        }

        return new UserResponse(createToken(user));
    }

    public UserResponse googleLogin(GoogleLoginRequest request, UserRole role) {
        String googleId = googleService.verifyGoogleToken(request.getIdToken());

        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setGoogleId(googleId);
                    newUser.setEmail("google_" + googleId); // todo: extract email and change to valid email
                    newUser.setRole(role);
                    return userRepository.save(newUser);
                });

        return new UserResponse(createToken(user));
    }

    private void validateUserExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("User already exists");
        }
    }

    private String createToken(User user) {
        Map<String, Object> claims = Map.of(
                "role", user.getRole());
        return jwtUtil.generateToken(user.getEmail(), claims);
    }
}
