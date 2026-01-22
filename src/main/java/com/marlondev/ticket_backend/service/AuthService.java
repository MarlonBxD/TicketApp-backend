package com.marlondev.ticket_backend.service;


import com.marlondev.ticket_backend.infrastructure.criteriafilter.UserSearchCriteria;
import com.marlondev.ticket_backend.infrastructure.dto.request.LoginRequest;
import com.marlondev.ticket_backend.infrastructure.dto.request.RegisterRequest;
import com.marlondev.ticket_backend.infrastructure.dto.response.TokenResponse;
import com.marlondev.ticket_backend.infrastructure.entity.Role;
import com.marlondev.ticket_backend.infrastructure.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.Set;
import java.util.UUID;

public interface AuthService {

    TokenResponse registerUser(RegisterRequest request);
    TokenResponse login(LoginRequest request);

    Page<User> findAll(Integer page,
                       Integer pageSize,
                       String sortBy,
                       Sort.Direction sortDirection,
                       UserSearchCriteria criteria);

    User findById(UUID id);

    User updateUser(UUID id, RegisterRequest request);

    void deleteUser(UUID id);

    User updateUserRoles(UUID id, Set<Role> roles);

    User toggleUserStatus(UUID id, Boolean active);
}
