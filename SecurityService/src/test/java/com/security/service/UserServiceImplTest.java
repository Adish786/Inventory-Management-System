package com.security.service;

import com.security.entity.Role;
import com.security.entity.User;
import com.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final String testEmail = "test@example.com";
    private final String notFoundEmail = "notfound@example.com";
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail(testEmail);
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // Act
        UserDetailsService detailsService = userService.getUserDetails();
        UserDetails userDetails = detailsService.loadUserByUsername(testEmail);

        // Assert
        assertNotNull(userDetails);
        assertEquals(testEmail, userDetails.getUsername());
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(notFoundEmail)).thenReturn(Optional.empty());

        // Act & Assert
        UserDetailsService detailsService = userService.getUserDetails();
        assertThrows(UsernameNotFoundException.class, () -> {
            detailsService.loadUserByUsername(notFoundEmail);
        });
        verify(userRepository).findByEmail(notFoundEmail);
    }

    @Test
    void loadUserByUsername_ShouldThrowExceptionWithCorrectMessage_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(notFoundEmail)).thenReturn(Optional.empty());

        // Act & Assert
        UserDetailsService detailsService = userService.getUserDetails();
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            detailsService.loadUserByUsername(notFoundEmail);
        });

        assertEquals("Use not found", exception.getMessage());
    }
}
