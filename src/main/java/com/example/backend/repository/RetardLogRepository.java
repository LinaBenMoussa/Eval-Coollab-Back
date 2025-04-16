package com.example.backend.repository;

import com.example.backend.entity.RetardLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;


@Repository
public interface RetardLogRepository extends JpaRepository<RetardLog, Long> {
    Optional<RetardLog> findByCollaborateur_MatriculeAndDateRetard(String matricule, LocalDate dateRetard);
}

