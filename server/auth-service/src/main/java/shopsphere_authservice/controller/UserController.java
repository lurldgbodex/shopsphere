package shopsphere_authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopsphere_authservice.dto.response.UserDto;
import shopsphere_authservice.service.UserService;
import shopsphere.shared.dto.GetUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getProfile(@RequestBody @Valid GetUser request) {
        return ResponseEntity.ok(userService.getUserDetails(request));
    }
}
