package com.khaphi.spotifycloneserver.service;

import com.khaphi.spotifycloneserver.entity.User;
import com.khaphi.spotifycloneserver.repository.UserRepository;
import com.khaphi.spotifycloneserver.request.AuthRequest;
import com.khaphi.spotifycloneserver.request.RegisterRequest;
import com.khaphi.spotifycloneserver.response.AuthResponse;
import com.khaphi.spotifycloneserver.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .role(request.getRole())
                .createAt(request.getCreateAt())
                .build();
        User savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefeshToken(user);
        var userResponse = UserResponse.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .createAt(savedUser.getCreateAt())
                .gender(savedUser.getGender())
                .role(savedUser.getRole())
                .build();
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        var jwtToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefeshToken(user);
        var userResponse = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createAt(user.getCreateAt())
                .gender(user.getGender())
                .role(user.getRole())
                .build();
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .build();
    }
}
