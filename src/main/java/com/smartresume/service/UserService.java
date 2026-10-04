package com.smartresume.service;

import com.smartresume.dto.AuthResponse;
import com.smartresume.dto.LoginRequest;
import com.smartresume.exception.ResourceNotFoundException;
import com.smartresume.model.User;
import com.smartresume.repository.UserRepository;
import com.smartresume.util.ModelMapperUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapperUtil modelMapperUtil;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ModelMapperUtil modelMapperUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapperUtil = modelMapperUtil;
    }

    @Transactional(readOnly = true)
    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid username or password");
        }

        // Return user with generated session token
        String token = "srs_auth_" + UUID.randomUUID().toString();
        return modelMapperUtil.toAuthResponse(user, token);
    }

    @Transactional(readOnly = true)
    public AuthResponse getCurrentUser() {
        User user = userRepository.findByUsername("admin")
                .orElseGet(() -> userRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No default user found")));

        return modelMapperUtil.toAuthResponse(user, "srs_default_session");
    }
}
