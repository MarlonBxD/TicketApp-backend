package com.marlondev.ticket_backend.infrastructure.repository;

import com.marlondev.ticket_backend.infrastructure.criteriafilter.TicketSearchCriteria;
import com.marlondev.ticket_backend.infrastructure.entity.Ticket;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TicketSpecification {

    private TicketSpecification(){
        throw new UnsupportedOperationException("Utility class");
    }
    public static Specification<Ticket> id(UUID id) {
        return id == null
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
    }
    public static Specification<Ticket> title(String title) {
        return title == null
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }
    public static Specification<Ticket> description(String description) {
        return description == null
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("description"), "%" + description + "%");
    }

    public static Specification<Ticket> status(String status) {
        return status == null
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }
    public static Specification<Ticket> createdBy(UUID createdBy) {
        return createdBy == null
                ? null
                : (root, query, cb) ->
                cb.equal(root.get("createdBy").get("id"), createdBy);
    }

    public static Specification<Ticket> assignedToId(UUID assignedTo) {
        return assignedTo == null
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("assignedTo"), assignedTo);
    }


    public static Specification<Ticket> buildSpecification(TicketSearchCriteria criteria) {
        Specification<Ticket> spec = null;
        spec = addSpecification(spec, id(criteria.getId()));
        spec = addSpecification(spec, title(criteria.getTitle()));
        spec = addSpecification(spec, description(criteria.getDescription()));
        spec = addSpecification(spec, status(criteria.getStatus()));
        spec = addSpecification(spec, createdBy(criteria.getCreatedById()));
        spec = addSpecification(spec, assignedToId(criteria.getAssignedToId()));

        return spec;
    }

    private static Specification<Ticket> addSpecification(
            Specification<Ticket> existingSpec,
            Specification<Ticket> newSpec) {
        if (newSpec == null) {
            return existingSpec;
        }
        return existingSpec == null ? newSpec : existingSpec.and(newSpec);
    }

}
