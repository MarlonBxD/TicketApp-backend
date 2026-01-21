package com.marlondev.ticket_backend.infrastructure.repository;


import com.marlondev.ticket_backend.infrastructure.criteriafilter.UserSearchCriteria;
import com.marlondev.ticket_backend.infrastructure.entity.Role;
import com.marlondev.ticket_backend.infrastructure.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;
import java.util.UUID;

public class UserSpecification {

    private UserSpecification(){
        throw new UnsupportedOperationException("Utility class");
    }

    public static Specification<User> username (String username) {
    return username == null
            ? null
            : (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("username"), username);
    }
    public static Specification<User> id(UUID id) {
        return id == null
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
    }
    public static Specification<User> roles(Set<Role> roles) {
        return (root, query, cb) -> {
            if (roles == null || roles.isEmpty()) {
                return null;
            }

            query.distinct(true);
            return root.join("roles").in(roles);
        };
    }

    public static Specification<User> active(Boolean active) {
        return active == null
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("enabled"), active);
    }
    public static Specification<User> email(String email) {
        return email == null
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email);
    }
    public static Specification<User> firstName(String firstName) {
        return firstName == null
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("firstName"), firstName);
    }
    public static Specification<User> lastName(String lastName) {
        return lastName == null
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("lastName"), lastName);
    }
    public static Specification<User> phoneNumber(String phoneNumber) {
        return phoneNumber == null
                ? null
                : (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("phoneNumber"), phoneNumber);
    }

    public static Specification<User> buildSpecification(UserSearchCriteria criteria) {
        Specification<User> spec = null;
        spec = addSpecification(spec, username(criteria.getUsername()));
        spec = addSpecification(spec, id(criteria.getId()));
        spec = addSpecification(spec, roles(criteria.getRoles()));
        spec = addSpecification(spec, active(criteria.getActive()));
        spec = addSpecification(spec, email(criteria.getEmail()));
        spec = addSpecification(spec, firstName(criteria.getFirstName()));
        spec = addSpecification(spec, lastName(criteria.getLastName()));
        spec = addSpecification(spec, phoneNumber(criteria.getPhone()));

        return spec;
    }

    private static Specification<User> addSpecification(
            Specification<User> existingSpec,
            Specification<User> newSpec) {
        if (newSpec == null) {
            return existingSpec;
        }
        return existingSpec == null ? newSpec : existingSpec.and(newSpec);
    }
}
