package shopsphere_authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shopsphere_authservice.dto.response.UserDto;
import shopsphere_authservice.entity.User;
import shopsphere_authservice.repository.UserRepository;
import shopsphere_shared.dto.GetUser;
import shopsphere_shared.exceptions.NotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto getUserDetails(GetUser request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("user not found"));

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .google_id(user.getGoogleId())
                .created_at(user.getCreatedAt())
                .updated_at(user.getUpdatedAt())
                .build();
    }
}
