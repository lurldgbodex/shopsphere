package shopsphere_authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import shopsphere_authservice.dto.request.GoogleLoginRequest;
import shopsphere_authservice.dto.request.LoginRequest;
import shopsphere_authservice.dto.request.RefreshTokenRequest;
import shopsphere_authservice.dto.request.RegisterRequest;
import shopsphere_authservice.dto.response.UserResponse;
import shopsphere_authservice.enums.UserRole;
import shopsphere_authservice.service.AuthService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request, UserRole.USER));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/google-login")
    public ResponseEntity<UserResponse> googleLogin(@RequestBody @Valid GoogleLoginRequest request) {
        return ResponseEntity.ok(authService.googleLogin(request, UserRole.USER));
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
