package com.marlondev.ticket_backend.infrastructure.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con token JWT")
public class TokenResponse {

    @Schema(description = "Estado de la respuesta", example = "200")
    private int httpCode;

    @Schema(description = "Token de acceso JWT", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String type;

    @Schema(description = "Token de refresco", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    private String firstName;
    private String lastName;
}
