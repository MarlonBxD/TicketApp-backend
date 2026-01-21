package com.marlondev.ticket_backend.infrastructure.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {

    private UUID id;

    @Size(max = 255)
    @NotNull(message = "por favor debe ingresar un titulo")
    private String title;

    private String description;

    @Size(max = 20)
    @NotNull(message = "el estado es obligatorio")
    private String status;

    private UUID assignedToId;
}
