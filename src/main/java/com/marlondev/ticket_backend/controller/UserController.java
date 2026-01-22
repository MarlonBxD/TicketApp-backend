package com.marlondev.ticket_backend.controller;

import com.marlondev.ticket_backend.infrastructure.criteriafilter.UserSearchCriteria;
import com.marlondev.ticket_backend.infrastructure.dto.request.RegisterRequest;
import com.marlondev.ticket_backend.infrastructure.dto.response.DefaultResponse;
import com.marlondev.ticket_backend.infrastructure.entity.Role;
import com.marlondev.ticket_backend.infrastructure.entity.User;
import com.marlondev.ticket_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs para la gestión de usuarios del sistema")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {

    private final AuthService authService;

    @GetMapping
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Retorna una lista paginada de usuarios con opciones de filtrado y ordenamiento. Solo accesible por administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = DefaultResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado - Requiere rol ADMIN"
            )
    })
    public ResponseEntity<DefaultResponse<Page<User>>> getAllUsers(
            @Parameter(description = "Número de página (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,

            @Parameter(description = "Campo por el cual ordenar", example = "username")
            @RequestParam(required = false) String sortBy,

            @Parameter(description = "Dirección de ordenamiento (ASC/DESC)")
            @RequestParam(required = false) Sort.Direction sortDirection,

            @Parameter(description = "Filtrar por ID de usuario")
            @RequestParam(required = false) String id,

            @Parameter(description = "Filtrar por nombre de usuario")
            @RequestParam(required = false) String username,

            @Parameter(description = "Filtrar por email")
            @RequestParam(required = false) String email,

            @Parameter(description = "Filtrar por nombre")
            @RequestParam(required = false) String firstName,

            @Parameter(description = "Filtrar por apellido")
            @RequestParam(required = false) String lastName,

            @Parameter(description = "Filtrar por teléfono")
            @RequestParam(required = false) String phone,

            @Parameter(description = "Filtrar por estado activo/inactivo")
            @RequestParam(required = false) Boolean active
    ) {
        UserSearchCriteria criteria = UserSearchCriteria.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .active(active)
                .build();

        Page<User> users = authService.findAll(page, pageSize, sortBy, sortDirection, criteria);

        DefaultResponse<Page<User>> response = DefaultResponse.<Page<User>>builder()
                .error(false)
                .message("Users retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .httpCode(HttpStatus.OK.value())
                .body(users)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Retorna los detalles de un usuario específico. Accesible por administradores y personal de soporte."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = DefaultResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado - Requiere rol ADMIN o SUPPORT"
            )
    })
    public ResponseEntity<DefaultResponse<User>> getUserById(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable UUID id
    ) {
        User user = authService.findById(id);

        DefaultResponse<User> response = DefaultResponse.<User>builder()
                .error(false)
                .message("User retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .httpCode(HttpStatus.OK.value())
                .body(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza toda la información de un usuario existente. Solo accesible por administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = DefaultResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado - Requiere rol ADMIN"
            )
    })
    public ResponseEntity<DefaultResponse<User>> updateUser(
            @Parameter(description = "ID del usuario a actualizar", required = true)
            @PathVariable UUID id,

            @Parameter(description = "Datos actualizados del usuario", required = true)
            @Valid @RequestBody RegisterRequest request
    ) {
        User updatedUser = authService.updateUser(id, request);

        DefaultResponse<User> response = DefaultResponse.<User>builder()
                .error(false)
                .message("User updated successfully")
                .httpStatus(HttpStatus.OK)
                .httpCode(HttpStatus.OK.value())
                .body(updatedUser)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina permanentemente un usuario del sistema. Solo accesible por administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado - Requiere rol ADMIN"
            )
    })
    public ResponseEntity<DefaultResponse<Void>> deleteUser(
            @Parameter(description = "ID del usuario a eliminar", required = true)
            @PathVariable UUID id
    ) {
        authService.deleteUser(id);

        DefaultResponse<Void> response = DefaultResponse.<Void>builder()
                .error(false)
                .message("User deleted successfully")
                .httpStatus(HttpStatus.OK)
                .httpCode(HttpStatus.OK.value())
                .body(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/roles")
    @Operation(
            summary = "Actualizar roles de usuario",
            description = "Actualiza los roles asignados a un usuario específico. Solo accesible por administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Roles actualizados exitosamente",
                    content = @Content(schema = @Schema(implementation = DefaultResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Roles inválidos"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado - Requiere rol ADMIN"
            )
    })
    public ResponseEntity<DefaultResponse<User>> updateUserRoles(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable UUID id,

            @Parameter(description = "Conjunto de roles a asignar", required = true)
            @RequestBody Set<Role> roles
    ) {
        User updatedUser = authService.updateUserRoles(id, roles);

        DefaultResponse<User> response = DefaultResponse.<User>builder()
                .error(false)
                .message("User roles updated successfully")
                .httpStatus(HttpStatus.OK)
                .httpCode(HttpStatus.OK.value())
                .body(updatedUser)
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Cambiar estado de usuario",
            description = "Activa o desactiva un usuario en el sistema. Solo accesible por administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado de usuario actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = DefaultResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetro de estado inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado - Requiere rol ADMIN"
            )
    })
    public ResponseEntity<DefaultResponse<User>> toggleUserStatus(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable UUID id,

            @Parameter(description = "Estado activo (true) o inactivo (false)", required = true, example = "true")
            @RequestParam Boolean active
    ) {
        User updatedUser = authService.toggleUserStatus(id, active);

        DefaultResponse<User> response = DefaultResponse.<User>builder()
                .error(false)
                .message("User status updated successfully")
                .httpStatus(HttpStatus.OK)
                .httpCode(HttpStatus.OK.value())
                .body(updatedUser)
                .build();

        return ResponseEntity.ok(response);
    }
}