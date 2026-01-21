package com.marlondev.ticket_backend.controller;

import com.marlondev.ticket_backend.infrastructure.criteriafilter.TicketSearchCriteria;
import com.marlondev.ticket_backend.infrastructure.dto.request.TicketRequest;
import com.marlondev.ticket_backend.infrastructure.dto.response.DefaultResponse;
import com.marlondev.ticket_backend.infrastructure.entity.Ticket;
import com.marlondev.ticket_backend.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Gestión de tickets del sistema")
public class TicketController {

    private final TicketService ticketService;

    @Operation(
            summary = "Listar tickets",
            description = "Obtiene un listado paginado de tickets con filtros opcionales"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = Ticket.class)))
    })
    @GetMapping
    public ResponseEntity<DefaultResponse<Page<Ticket>>> findAll(
            @Parameter(description = "Número de página (0-based)")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Cantidad de registros por página")
            @RequestParam(defaultValue = "10") Integer pageSize,

            @Parameter(description = "Campo para ordenar (ej: createdAt)")
            @RequestParam(required = false) String sortBy,

            @Parameter(description = "Dirección de orden ASC o DESC")
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,

            TicketSearchCriteria criteria
    ) {
        Page<Ticket> result = ticketService.findAll(
                page, pageSize, sortBy, sortDirection, criteria
        );

        DefaultResponse<Page<Ticket>> response = DefaultResponse.<Page<Ticket>>builder()
                .error(false)
                .message("Tickets retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .httpCode(HttpStatus.OK.value())
                .body(result)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener ticket por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DefaultResponse<Ticket>> findById(
            @PathVariable UUID id
    ) {
        Ticket ticket = ticketService.findById(id);

        DefaultResponse<Ticket> response = DefaultResponse.<Ticket>builder()
                .error(false)
                .message("Ticket retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .httpCode(HttpStatus.OK.value())
                .body(ticket)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ticket creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<DefaultResponse<Ticket>> create(
            @Valid @RequestBody TicketRequest request
    ) {
        Ticket ticket = ticketService.save(request);

        DefaultResponse<Ticket> response = DefaultResponse.<Ticket>builder()
                .error(false)
                .message("Ticket created successfully")
                .httpStatus(HttpStatus.CREATED)
                .httpCode(HttpStatus.CREATED.value())
                .body(ticket)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket actualizado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    @PutMapping("update/{id}")
    public ResponseEntity<DefaultResponse<Ticket>> update(
            @PathVariable UUID id,
            @RequestBody TicketRequest request
    ) {
        Ticket ticket = ticketService.update(id, request);

        DefaultResponse<Ticket> response = DefaultResponse.<Ticket>builder()
                .error(false)
                .message("Ticket updated successfully")
                .httpStatus(HttpStatus.OK)
                .httpCode(HttpStatus.OK.value())
                .body(ticket)
                .build();

        return ResponseEntity.ok(response);
    }
}