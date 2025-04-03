package com.example.backend.repository;

import com.example.backend.entity.Conge;
import com.example.backend.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface CongeRepository extends JpaRepository<Conge, Long>, JpaSpecificationExecutor<Conge> {
    List<Conge> findByCollaborateur_ManagerId(Long managerId);

    List<Conge> findByCollaborateur_Id(Long collaborateurId);

    List<Conge> findByCollaborateur_ManagerIdAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
            Long managerId, LocalDateTime dateDebut, LocalDateTime dateFin);

    @Query("SELECT c FROM Conge c WHERE c.collaborateur.matricule = :matricule " +
            "AND :selectedDate BETWEEN CAST(c.dateDebut AS localdate) AND CAST(c.dateFin AS localdate)")
    Conge findByCollaborateurAndDateRange(
            @Param("matricule") String matricule,
            @Param("selectedDate") LocalDate selectedDate
    );
}
