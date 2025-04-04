package com.example.backend.repository;

import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByRole(String role);

    List<User> findByRoleAndManagerId(String role, Long managerId);

    List<User> findByManagerId(Long managerId);

    User findByMatricule(String matricule);

}

