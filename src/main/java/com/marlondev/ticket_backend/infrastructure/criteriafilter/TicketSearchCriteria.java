package com.marlondev.ticket_backend.infrastructure.criteriafilter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ParameterObject
public class TicketSearchCriteria {

    private UUID id;
    private String title;
    private String description;
    private String status;
    private UUID createdById;
    private UUID assignedToId;
}
