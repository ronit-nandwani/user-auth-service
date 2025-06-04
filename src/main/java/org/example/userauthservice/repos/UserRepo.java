package org.example.userauthservice.repos;

import org.example.userauthservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmailEquals(String email);
}
