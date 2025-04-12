package com.security.service;


import com.security.dto.JwtAuthenticationResponse;
import com.security.dto.RefreshTokenRequest;
import com.security.dto.SignInRequest;
import com.security.dto.SignupRequest;
import com.security.entity.User;
import com.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImp authenticationService;
    @Mock
    private   RefreshTokenRequest refreshTokenRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignupUser_shouldSaveAndReturnUser() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setEmail("john@example.com");
        signupRequest.setPassword("rawpass");

        User savedUser = new User();
        savedUser.setFirstname("John");
        savedUser.setLastName("Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword("encodedpass");

        when(passwordEncoder.encode("rawpass")).thenReturn("encodedpass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authenticationService.signupUser(signupRequest);

        assertEquals("John", result.getFirstname());
        assertEquals("Doe", result.getLastName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("encodedpass", result.getPassword());
    }

    @Test
    void testSignInRequest_shouldReturnJwtTokens() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("john@example.com");
        signInRequest.setPass("password");

        User user = new User();
        user.setEmail("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(anyMap(), eq(user))).thenReturn("refresh-token");

        JwtAuthenticationResponse response = authenticationService.signInRequest(signInRequest);

        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testRefreshTokenRequest_shouldReturnNewTokensIfValid() {

        refreshTokenRequest.setRefreshToken("valid-refresh-token");

        User user = new User();
        user.setEmail("john@example.com");

        when(jwtService.extractUserName("valid-refresh-token")).thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("valid-refresh-token", user)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(anyMap(), eq(user))).thenReturn("new-refresh-token");
        assertEquals("john@example.com", user.getEmail());

    }

}
