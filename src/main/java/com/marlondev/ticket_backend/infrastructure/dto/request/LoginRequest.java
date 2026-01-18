package com.marlondev.ticket_backend.infrastructure.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo para solicitud de inicio de sesión")
public class LoginRequest {

    @Schema(description = "Nombre de usuario", example = "jdoe")
    private String username;

    @Schema(description = "Contraseña del usuario", example = "123456")
    private String password;
}
