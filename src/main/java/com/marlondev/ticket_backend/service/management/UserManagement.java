package com.marlondev.ticket_backend.service.management;

import com.marlondev.ticket_backend.exception.ResourceNotFoundException;
import com.marlondev.ticket_backend.infrastructure.dto.request.LoginRequest;
import com.marlondev.ticket_backend.infrastructure.dto.request.RegisterRequest;
import com.marlondev.ticket_backend.infrastructure.dto.response.TokenResponse;
import com.marlondev.ticket_backend.infrastructure.entity.Role;
import com.marlondev.ticket_backend.infrastructure.entity.User;
import com.marlondev.ticket_backend.infrastructure.repository.UserRepository;
import com.marlondev.ticket_backend.service.JwtService;
import com.marlondev.ticket_backend.service.authService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class UserManagement implements authService {

        private final UserRepository userRepository;
        private final Logger logger = Logger.getLogger(UserManagement.class.getName());
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final org.springframework.security.authentication.AuthenticationManager authenticationManager;

        @Override
        public TokenResponse registerUser(RegisterRequest request) {

                if (userRepository.existsByUsername((request.getUsername()))) {
                        throw new ResourceNotFoundException(
                                        "Username already exists: " + request.getUsername());
                }
                if (userRepository.existsByEmail((request.getEmail()))) {
                        throw new ResourceNotFoundException(
                                        "Email already exists: " + request.getEmail());
                }

                var user = User.builder()
                                .username(request.getUsername())
                                .email(request.getEmail())
                                .lastName(request.getLastName())
                                .firstName(request.getFirstName())
                                .phone(request.getPhone())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .build();

                user.setActive(true);
                var sabeUser = userRepository.save(user);
                var jwtToken = jwtService.generateToken(sabeUser);
                var refreshToken = jwtService.generateRefreshToken(sabeUser);
                TokenResponse tokenResponse;
                return tokenResponse = TokenResponse.builder()
                        .httpCode(HttpStatus.CREATED.value())
                        .type("Bearer")
                        .firstName(sabeUser.getFirstName())
                        .lastName(sabeUser.getLastName())
                        .token(jwtToken)
                        .refreshToken(refreshToken).build();
        }

        @Override
        public TokenResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsername(),
                                                request.getPassword()));

                User user = userRepository.findByUsername(request.getUsername())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                String jwtToken = jwtService.generateToken(user);
                String refreshToken = jwtService.generateRefreshToken(user);

                return TokenResponse.builder()
                                .token(jwtToken)
                                .refreshToken(refreshToken)
                                .httpCode(HttpStatus.OK.value())
                                .type("Bearer")
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .build();
        }

        @Override
        public User updateUser(UUID id, RegisterRequest request) {
                return null;
        }

        @Override
        public void deleteUser(UUID id) {

        }

        @Override
        public User updateUserRoles(UUID id, Set<Role> roles) {
                return null;
        }
}
