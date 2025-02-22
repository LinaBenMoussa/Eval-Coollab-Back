package com.example.backend.repository;

import com.example.backend.entity.Parametre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametreRepository extends JpaRepository<Parametre, Long> {
    Optional<Parametre> findByCle(String cle);
}

