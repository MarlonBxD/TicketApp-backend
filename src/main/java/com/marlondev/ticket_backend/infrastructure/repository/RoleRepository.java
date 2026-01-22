package com.marlondev.ticket_backend.infrastructure.repository;

import com.marlondev.ticket_backend.infrastructure.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Role findByName(String name);
}
