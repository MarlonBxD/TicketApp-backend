package com.marlondev.ticket_backend.service;

import com.marlondev.ticket_backend.infrastructure.dto.request.LoginRequest;
import com.marlondev.ticket_backend.infrastructure.dto.request.RegisterRequest;
import com.marlondev.ticket_backend.infrastructure.dto.response.TokenResponse;
import com.marlondev.ticket_backend.infrastructure.entity.Role;
import com.marlondev.ticket_backend.infrastructure.entity.User;

import java.util.Set;
import java.util.UUID;

public interface authService {

    TokenResponse registerUser(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    User updateUser(UUID id, RegisterRequest request);
    void deleteUser(UUID id);
    User updateUserRoles(UUID id, Set<Role> roles);
}
