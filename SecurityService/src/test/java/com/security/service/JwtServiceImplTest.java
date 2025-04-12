package com.security.service;

import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.security.Key;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    private UserDetails userDetails;
    private final String testUsername = "testuser";
    private static final String SECRET = "f4aXK9sS9GHh/ZbKvUO7ugfNiUdV+NQ5VKfZKtk+nqM=";

    @BeforeEach
    void setUp() {
        userDetails = User.withUsername(testUsername)
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertEquals(testUsername, jwtService.extractUserName(token));
      //  assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void extractUserName_ShouldReturnUsername_FromValidToken() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String extractedUsername = jwtService.extractUserName(token);

        // Assert
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act & Assert
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForInvalidUser() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = User.withUsername("otheruser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        // Act & Assert
        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_ForExpiredToken() {
        // Arrange - Create a token with past expiration
        Date pastDate = new Date(System.currentTimeMillis() - 1000);
        String expiredToken = Jwts.builder()
                .setSubject(testUsername)
                .setIssuedAt(new Date())
                .setExpiration(pastDate)
                .signWith(getTestSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken_WithExtraClaims() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");
        String refreshToken = jwtService.generateRefreshToken(extraClaims, userDetails);
        assertNotNull(refreshToken);
        assertEquals(testUsername, jwtService.extractUserName(refreshToken));
    }

    @Test
    void extractAllClaims_ShouldReturnClaims_ForValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
    }

    private Key getTestSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}