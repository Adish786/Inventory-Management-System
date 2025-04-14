package com.security.service;

import com.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ExecutorService userExecutor = Executors.newFixedThreadPool(4); // Thread pool for concurrency
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable
    public UserDetailsService getUserDetails() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                try {
                    return CompletableFuture.supplyAsync(() ->
                                    userRepository.findByEmail(username)
                                            .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                            , userExecutor).get(); // wait for result
                } catch (InterruptedException | ExecutionException e) {
                 //   log.error("Error loading user asynchronously", e);
                    throw new UsernameNotFoundException("Failed to load user: " + username);
                }
            }
        };
    }
}
