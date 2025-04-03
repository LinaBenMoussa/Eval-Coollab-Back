package com.example.backend.repository;

import com.example.backend.entity.SaisieTemps;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface SaisieTempsRepository extends JpaRepository<SaisieTemps, Long>, JpaSpecificationExecutor<SaisieTemps> {
    List<SaisieTemps> findByIssueId(Long issueId);

    List<SaisieTemps> findByCollaborateur_ManagerId(Long managerId);

    List<SaisieTemps> findByCollaborateurAndDate(User collaborateur, LocalDate date);
}
