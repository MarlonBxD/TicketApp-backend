package com.marlondev.ticket_backend.service.management;

import com.marlondev.ticket_backend.exception.ResourceNotFoundException;
import com.marlondev.ticket_backend.infrastructure.criteriafilter.UserSearchCriteria;
import com.marlondev.ticket_backend.infrastructure.dto.request.LoginRequest;
import com.marlondev.ticket_backend.infrastructure.dto.request.RegisterRequest;
import com.marlondev.ticket_backend.infrastructure.dto.request.UserRequest;
import com.marlondev.ticket_backend.infrastructure.dto.response.TokenResponse;
import com.marlondev.ticket_backend.infrastructure.entity.Role;
import com.marlondev.ticket_backend.infrastructure.entity.User;
import com.marlondev.ticket_backend.infrastructure.repository.UserRepository;
import com.marlondev.ticket_backend.infrastructure.repository.UserSpecification;
import com.marlondev.ticket_backend.service.JwtService;
import com.marlondev.ticket_backend.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class UserManagement implements AuthService {

        private final UserRepository userRepository;
        private final Logger logger = Logger.getLogger(UserManagement.class.getName());
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final org.springframework.security.authentication.AuthenticationManager authenticationManager;

        private final UserRepository repository;

        @Override
        public Page<User> findAll(Integer page,
                                  Integer pageSize,
                                  String sortBy,
                                  Sort.Direction sortDirection,
                                  UserSearchCriteria criteria) {

                Sort sort = (sortBy == null || sortBy.isBlank())
                        ? Sort.unsorted()
                        : Sort.by(sortDirection != null ? sortDirection : Sort.Direction.ASC,
                        sortBy.split(","));

                Pageable pageable = PageRequest.of(page, pageSize, sort);
                Specification<User> spec = UserSpecification.buildSpecification(criteria);

                return repository.findAll(spec, pageable);
        }

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
                        .user(convertUserToUserRequest(sabeUser))
                        .type("Bearer")
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
                                .httpCode(HttpStatus.OK.value())
                                .user(convertUserToUserRequest(user))
                                .token(jwtToken)
                                .refreshToken(refreshToken)
                                .type("Bearer")
                                .build();
        }

        @Override
        public User updateUser(UUID id, RegisterRequest request) {

                var user = userRepository.findById(id)
                        .orElseThrow(()-> new ResourceNotFoundException("User not found"));
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setEmail(request.getEmail());
                user.setPhone(request.getPhone());
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setActive(request.getActive());
                user.setUpdatedAt(java.time.LocalDateTime.now());
                return repository.save(user);
        }

        @Override
        public void deleteUser(UUID id) {
                User user = userRepository.findById(id)
                        .orElseThrow(()-> new ResourceNotFoundException("User not found"));
                repository.delete(user);
        }

        @Override
        public User updateUserRoles(UUID id, Set<Role> roles) {
               var user = userRepository.findById(id)
                        .orElseThrow(()-> new ResourceNotFoundException("User not found"));
                user.setRoles(roles);
                return repository.save(user);
        }

        @Override
        public User findById(UUID id) {
                return userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        }

        @Override
        public User toggleUserStatus(UUID id, Boolean active) {
                User user = userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

                user.setActive(active);
                user.setUpdatedAt(java.time.LocalDateTime.now());

                return repository.save(user);
        }

        private UserRequest convertUserToUserRequest(User user){
                UserRequest userRequest = new UserRequest();
                userRequest.setId(user.getId());
                userRequest.setFirstName(user.getFirstName());
                userRequest.setLastName(user.getLastName());
                userRequest.setEmail(user.getEmail());
                userRequest.setPhone(user.getPhone());
                userRequest.setUsername(user.getUsername());
                userRequest.setRoles(user.getRoles());
                return userRequest;

        }
}
