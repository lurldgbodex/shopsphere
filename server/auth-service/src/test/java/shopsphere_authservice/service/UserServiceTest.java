package shopsphere_authservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shopsphere_authservice.dto.response.UserDto;
import shopsphere_authservice.entity.User;
import shopsphere_authservice.enums.UserRole;
import shopsphere_authservice.repository.UserRepository;
import shopsphere_shared.dto.GetUser;
import shopsphere_shared.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @InjectMocks private UserService underTest;

    @Test
    void getUserDetail_success() {
        GetUser request = new GetUser("test@user.com");
        User user = User.builder()
                .id(5L)
                .email(request.email())
                .googleId("google-id")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        UserDto response = underTest.getUserDetails(request);

        assertEquals(user.getId(), response.getId());
        assertEquals(user.getRole(), response.getRole());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getGoogleId(), response.getGoogle_id());
        assertEquals(user.getCreatedAt(), response.getCreated_at());
        assertEquals(user.getUpdatedAt(), response.getUpdated_at());

        verify(userRepository).findByEmail(request.email());
    }

    @Test
    void getUserDetails_whenUserNotFound() {
        GetUser request = new GetUser("test@user.com");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () ->
                underTest.getUserDetails(request));

        assertEquals("user not found", ex.getMessage());
        verify(userRepository).findByEmail(request.email());
    }
}