package com.marlondev.ticket_backend.service.management;

import com.marlondev.ticket_backend.exception.ResourceNotFoundException;
import com.marlondev.ticket_backend.infrastructure.criteriafilter.TicketSearchCriteria;
import com.marlondev.ticket_backend.infrastructure.dto.request.TicketRequest;
import com.marlondev.ticket_backend.infrastructure.entity.Ticket;
import com.marlondev.ticket_backend.infrastructure.entity.User;
import com.marlondev.ticket_backend.infrastructure.repository.TicketRepository;
import com.marlondev.ticket_backend.infrastructure.repository.TicketSpecification;
import com.marlondev.ticket_backend.infrastructure.repository.UserRepository;
import com.marlondev.ticket_backend.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketManagement implements TicketService {

        private final TicketRepository ticketRepository;
        private final UserRepository userRepository;

        @Override
        public Page<Ticket> findAll(Integer page,
                        Integer pageSize,
                        String sortBy,
                        Sort.Direction sortDirection,
                        TicketSearchCriteria criteria) {

                Sort sort = (sortBy == null || sortBy.isBlank())
                                ? Sort.unsorted()
                                : Sort.by(sortDirection != null ? sortDirection : Sort.Direction.ASC,
                                                sortBy.split(","));

                Pageable pageable = PageRequest.of(page, pageSize, sort);
                Specification<Ticket> spec = TicketSpecification.buildSpecification(criteria);

                return ticketRepository.findAll(spec, pageable);
        }

        @Override
        public Ticket findById(UUID id) {
                return ticketRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Ticket"));
        }

        @Override
        public Ticket save(TicketRequest request) {
                User createdBy = userRepository.findById(request.getCreatedById())
                                .orElseThrow(() -> new ResourceNotFoundException("Creator user not found"));

                User assignedTo = null;
                if (request.getAssignedToId() != null) {
                        assignedTo = userRepository.findById(request.getAssignedToId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));
                }

                Ticket ticket = Ticket.builder()
                                .title(request.getTitle())
                                .description(request.getDescription())
                                .status(request.getStatus())
                                .createdBy(createdBy)
                                .assignedTo(assignedTo)
                                .createdAt(LocalDateTime.now())
                                .build();

                return ticketRepository.save(ticket);
        }

        @Override
        public Ticket update(UUID id, TicketRequest request) {
                Ticket ticket = ticketRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

                ticket.setTitle(request.getTitle());
                ticket.setDescription(request.getDescription());
                ticket.setStatus(request.getStatus());

                if (request.getAssignedToId() != null) {
                        User assignedTo = userRepository.findById(request.getAssignedToId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));
                        ticket.setAssignedTo(assignedTo);
                } else {
                        ticket.setAssignedTo(null);
                }

                // Don't update createdBy or createdAt typically, but if needed:
                // if (request.getCreatedById() != null) { ... }

                ticket.setUpdatedAt(LocalDateTime.now());
                return ticketRepository.save(ticket);
        }
}
