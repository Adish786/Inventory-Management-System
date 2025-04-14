package com.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private static final String SECRET = "f4aXK9sS9GHh/ZbKvUO7ugfNiUdV+NQ5VKfZKtk+nqM=";

    private final ExecutorService jwtExecutor = Executors.newFixedThreadPool(4);

    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return CompletableFuture.supplyAsync(() -> Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // 24 minutes
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact(), jwtExecutor).join();
    }

    @Override
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        CompletableFuture<String> usernameFuture = CompletableFuture.supplyAsync(() -> extractUserName(token), jwtExecutor);
        CompletableFuture<Boolean> expirationFuture = CompletableFuture.supplyAsync(() -> !isTokenExpired(token), jwtExecutor);

        return usernameFuture.join().equals(userDetails.getUsername()) && expirationFuture.join();
    }

    @Override
    @Cacheable
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return CompletableFuture.supplyAsync(() -> Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 604800000)) // 7 days
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact(), jwtExecutor).join();
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Key getSignKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
