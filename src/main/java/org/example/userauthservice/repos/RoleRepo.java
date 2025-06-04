package org.example.userauthservice.repos;

import org.example.userauthservice.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
}
