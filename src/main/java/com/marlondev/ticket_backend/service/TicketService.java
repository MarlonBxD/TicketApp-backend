package com.marlondev.ticket_backend.service;

import com.marlondev.ticket_backend.infrastructure.criteriafilter.TicketSearchCriteria;
import com.marlondev.ticket_backend.infrastructure.dto.request.TicketRequest;
import com.marlondev.ticket_backend.infrastructure.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public interface TicketService {
    Page<Ticket> findAll(Integer page,
                         Integer pageSize,
                         String sortBy,
                         Sort.Direction sortDirection,
                         TicketSearchCriteria criteria);

    Ticket findById(UUID id);

    Ticket save(TicketRequest request);

    Ticket update(UUID id, TicketRequest request);
}
