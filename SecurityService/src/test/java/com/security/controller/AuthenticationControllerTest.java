package com.security.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.security.dto.JwtAuthenticationResponse;
import com.security.dto.RefreshTokenRequest;
import com.security.dto.SignInRequest;
import com.security.dto.SignupRequest;
import com.security.entity.User;
import com.security.service.AuthenticationService;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;
    @Mock
    private RefreshTokenRequest refreshTokenRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void signup_ShouldReturnUser_WhenValidRequest() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName("john");
        signupRequest.setLastName("yeden");
        signupRequest.setEmail("john@example.com");
        signupRequest.setPassword("password");

        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setFirstname("Test User");

        when(authenticationService.signupUser(any(SignupRequest.class))).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void signIn_ShouldReturnJwtResponse_WhenValidCredentials() throws Exception {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName("john");
        signupRequest.setLastName("yeden");
        signupRequest.setEmail("john@example.com");
        signupRequest.setPassword("password");
        JwtAuthenticationResponse mockResponse = new JwtAuthenticationResponse("access-token", "refresh-token");

        when(authenticationService.signInRequest(any(SignInRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void refreshToken_ShouldReturnNewJwtResponse_WhenValidRefreshToken() throws Exception {

        JwtAuthenticationResponse mockResponse = new JwtAuthenticationResponse("new-access-token", "new-refresh-token");

        when(authenticationService.refreshTokenRequest(any(RefreshTokenRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }
}