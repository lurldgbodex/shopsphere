package shopsphere_authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shopsphere_authservice.dto.response.UserDto;
import shopsphere_authservice.enums.UserRole;
import shopsphere_authservice.service.UserService;
import shopsphere.shared.dto.GetUser;
import shopsphere.shared.exceptions.NotFoundException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    private ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserService userService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateMockmvc() {
        assertNotNull(mockMvc);
    }

    @Test
    void getProfile_success() throws Exception {
        GetUser request = new GetUser("test@email.com");
        UserDto userDto = UserDto.builder()
                .id(5L)
                .email("test@email.com")
                .google_id("google-id")
                .role(UserRole.VENDOR)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        when(userService.getUserDetails(request)).thenReturn(userDto);
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(get("/api/v1/profiles/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty())
                .andExpect(jsonPath("$.google_id").value(userDto.getGoogle_id()))
                .andExpect(jsonPath("$.role").value(userDto.getRole().toString()));
    }

    @Test
    void getUserDetail_userNotFound() throws Exception {
        GetUser request = new GetUser("test@email.com");

        when(userService.getUserDetails(request)).thenThrow(NotFoundException.class);
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(get("/api/v1/profiles/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isNotFound());
    }
}