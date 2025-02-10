package com.security.service;

import com.security.dto.JwtAuthenticationResponse;
import com.security.dto.RefreshTokenRequest;
import com.security.dto.SignInRequest;
import com.security.dto.SignupRequest;
import com.security.entity.User;

public interface AuthenticationService {
    User signupUser(SignupRequest signupRequest);
    JwtAuthenticationResponse signInRequest(SignInRequest signInRequest);
    JwtAuthenticationResponse refreshTokenRequest(RefreshTokenRequest refreshTokenRequest);
}
