package com.security.service;

import com.security.dto.JwtAuthenticationResponse;
import com.security.dto.RefreshTokenRequest;
import com.security.dto.SignInRequest;
import com.security.dto.SignupRequest;
import com.security.entity.User;
import com.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AuthenticationServiceImp implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public User signupUser(SignupRequest signupRequest) {
        User user = new User();
        user.setFirstname(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        return userRepository.save(user);
    }

    public JwtAuthenticationResponse signInRequest(SignInRequest signInRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPass()));
        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        CompletableFuture<String> tokenFuture = CompletableFuture.supplyAsync(() -> jwtService.generateToken(user));
        CompletableFuture<String> refreshTokenFuture = CompletableFuture.supplyAsync(() ->
                jwtService.generateRefreshToken(new HashMap<>(), user));
        String token = tokenFuture.join();
        String refreshToken = refreshTokenFuture.join();
        return new JwtAuthenticationResponse(token, refreshToken);
    }
    public JwtAuthenticationResponse refreshTokenRequest(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.extractUserName(refreshTokenRequest.getRefreshToken());
        CompletableFuture<Optional<User>> userFuture = CompletableFuture.supplyAsync(() ->
                userRepository.findByEmail(userEmail));
        User user = userFuture.join().orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (jwtService.isTokenValid(refreshTokenRequest.getRefreshToken(), user)) {
            CompletableFuture<String> tokenFuture = CompletableFuture.supplyAsync(() -> jwtService.generateToken(user));
            CompletableFuture<String> refreshTokenFuture = CompletableFuture.supplyAsync(() ->
                    jwtService.generateRefreshToken(new HashMap<>(), user));

            return new JwtAuthenticationResponse(tokenFuture.join(), refreshTokenFuture.join());
        }

        return null;
    }
}
