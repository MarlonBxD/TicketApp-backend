package com.marlondev.ticket_backend.controller;

import com.marlondev.ticket_backend.infrastructure.dto.request.LoginRequest;
import com.marlondev.ticket_backend.infrastructure.dto.request.RegisterRequest;
import com.marlondev.ticket_backend.infrastructure.dto.response.DefaultResponse;
import com.marlondev.ticket_backend.infrastructure.dto.response.TokenResponse;
import com.marlondev.ticket_backend.service.authService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro y autenticación de usuarios")
public class authController {

    private final authService authService;

    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario y devuelve un token JWT para autenticación inmediata")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos o usuario ya existe", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/register")
    public ResponseEntity<DefaultResponse<TokenResponse>> register(@Valid @RequestBody RegisterRequest request) {
        //TokenResponse tokenResponse = authService.registerUser(request);

        DefaultResponse<TokenResponse> response = DefaultResponse.<TokenResponse>builder()
                .error(false)
                .message("User registered successfully")
                .httpStatus(HttpStatus.CREATED)
                .httpCode(HttpStatus.CREATED.value())
                .body(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con sus credenciales y devuelve un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(loginResponse);
    }
}
