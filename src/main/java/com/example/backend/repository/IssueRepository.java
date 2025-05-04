package com.example.backend.repository;

import com.example.backend.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {
    List<Issue> findByCollaborateur_ManagerId(Long managerId);

    List<Issue> findByCollaborateur_Id(Long collaborateurId);

    @Query("SELECT i FROM Issue i WHERE i.dateEcheance < :now AND i.status.is_closed != true")
    List<Issue> findByDateEcheanceBeforeAndStatusNotClosed(LocalDateTime now);

    @Query("SELECT i FROM Issue i WHERE i.dateEcheance < :now AND i.status.is_closed != true AND i.isExpired = false")
    List<Issue> findNonExpiredIssues(LocalDateTime now);

    long countByProjectId(Long projectId);

    @Query("SELECT i FROM Issue i WHERE i.dateDebut BETWEEN :startDate AND :endDate AND i.collaborateur.id = :collaborateurId")
    List<Issue> findByCollaborateurAndDateDebutBetween(Long collaborateurId, LocalDateTime startDate, LocalDateTime endDate);




}

