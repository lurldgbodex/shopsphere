package shopsphere_authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shopsphere_authservice.dto.request.GoogleLoginRequest;
import shopsphere_authservice.dto.request.RegisterRequest;
import shopsphere_authservice.dto.response.UserResponse;
import shopsphere_authservice.enums.UserRole;
import shopsphere_authservice.service.AuthService;
import shopsphere.shared.exceptions.ConflictException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(VendorController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class VendorControllerTest {

    private ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;
    @MockitoBean private AuthService authService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Nested
    class RegisterUserTest {

        @Test
        void registerUser_success() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("test@user.com")
                    .password("P@ssw0rd98")
                    .build();

            String requestString = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/api/v1/auth/vendor/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isCreated());
        }

        @Test
        void registerUser_whenUserAlreadyExists() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("test@user.com")
                    .password("P@ssw0rd98")
                    .build();

            String requestString = objectMapper.writeValueAsString(request);

            when(authService.register(request, UserRole.VENDOR)).thenThrow(ConflictException.class);

            mockMvc.perform(post("/api/v1/auth/vendor/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("failure"));
        }

        @Test
        void registerUser_withNoData() throws Exception {
            RegisterRequest request = RegisterRequest.builder().build();

            String requestString = objectMapper.writeValueAsString(request);
            mockMvc.perform(post("/api/v1/auth/vendor/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors.email").value("email is required"))
                    .andExpect(jsonPath("$.errors.password").value("password is required"));
        }

        @Test
        void registerUser_withInvalidEmail() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("invalid-email")
                    .password("P@ssw0rd22.")
                    .build();

            String requestString = objectMapper.writeValueAsString(request);
            when(authService.register(request, UserRole.VENDOR)).thenThrow(ConflictException.class);

            mockMvc.perform(post("/api/v1/auth/vendor/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors.email").value("please provide a valid email"));
        }

        @Test
        void registerUser_withPasswordLessThan8Characters() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("test@user.com")
                    .password("p@ssM1.")
                    .build();

            String requestString = objectMapper.writeValueAsString(request);
            when(authService.register(request, UserRole.VENDOR)).thenThrow(ConflictException.class);

            mockMvc.perform(post("/api/v1/auth/vendor/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors.password")
                            .value("Password must be at least 8 with one uppercase, lowercase, number and special character."));
        }

        @Test
        void registerUser_passwordWithNoUpperCase() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("test@user.com")
                    .password("p@ssw0rd")
                    .build();

            String requestString = objectMapper.writeValueAsString(request);
            when(authService.register(request, UserRole.VENDOR)).thenThrow(ConflictException.class);

            mockMvc.perform(post("/api/v1/auth/vendor/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors.password")
                            .value("Password must be at least 8 with one uppercase, lowercase, number and special character."));
        }

        @Test
        void registerUser_passwordWithNoLowerCase() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("test@user.com")
                    .password("P@SSW0RD")
                    .build();

            String requestString = objectMapper.writeValueAsString(request);
            when(authService.register(request, UserRole.VENDOR)).thenThrow(ConflictException.class);

            mockMvc.perform(post("/api/v1/auth/vendor/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors.password")
                            .value("Password must be at least 8 with one uppercase, lowercase, number and special character."));
        }

        @Test
        void registerUser_passwordWithNoSpecialCharacter() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("test@user.com")
                    .password("Passworl3d")
                    .build();

            String requestString = objectMapper.writeValueAsString(request);
            when(authService.register(request, UserRole.VENDOR)).thenThrow(ConflictException.class);

            mockMvc.perform(post("/api/v1/auth/vendor/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors.password")
                            .value("Password must be at least 8 with one uppercase, lowercase, number and special character."));
        }

        @Test
        void registerUser_passwordWithNoDigit() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .email("test@user.com")
                    .password("Passwor!d.")
                    .build();

            String requestString = objectMapper.writeValueAsString(request);
            when(authService.register(request, UserRole.VENDOR)).thenThrow(ConflictException.class);

            mockMvc.perform(post("/api/v1/auth/vendor/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors.password")
                            .value("Password must be at least 8 with one uppercase, lowercase, number and special character."));
        }
    }

    @Nested
    class GoogleLoginTest {

        @Test
        void googleLogin_success() throws Exception {
            GoogleLoginRequest request = new GoogleLoginRequest();
            request.setId_token("id-token");

            UserResponse token = new UserResponse("dummy-token", "refresh-token");

            when(authService.googleLogin(request, UserRole.VENDOR)).thenReturn(token);

            String requestString = objectMapper.writeValueAsString(request);
            mockMvc.perform(post("/api/v1/auth/vendor/google-login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestString))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.access_token").value("dummy-token"))
                    .andExpect(jsonPath("$.refresh_token").value(token.refresh_token()));
        }
    }
}