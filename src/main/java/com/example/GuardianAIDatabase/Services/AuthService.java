package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.DTOs.AuthResponse;
import com.example.GuardianAIDatabase.DTOs.LoginRequest;
import com.example.GuardianAIDatabase.DTOs.RegisterRequest;
import com.example.GuardianAIDatabase.Entity.Parent;
import com.example.GuardianAIDatabase.Repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (parentRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Parent parent = new Parent();
        parent.setName(request.getName());
        parent.setEmail(request.getEmail());
        parent.setPassword(passwordEncoder.encode(request.getPassword()));
        parent.setPhoneNumber(request.getPhoneNumber());
        parent.setCreatedAt(LocalDateTime.now());

        parentRepository.save(parent);

        String token = jwtService.generateToken(parent.getEmail());
        return new AuthResponse(token, parent.getEmail(), parent.getName(), parent.getParentId());
    }

    public AuthResponse login(LoginRequest request) {
        Parent parent = parentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), parent.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(parent.getEmail());
        return new AuthResponse(token, parent.getEmail(), parent.getName(), parent.getParentId());
    }
}
