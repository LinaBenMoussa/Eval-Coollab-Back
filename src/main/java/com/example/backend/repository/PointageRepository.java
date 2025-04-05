package com.example.backend.repository;

import com.example.backend.entity.Conge;
import com.example.backend.entity.Pointage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PointageRepository extends JpaRepository<Pointage, Long>, JpaSpecificationExecutor<Pointage> {
    List<Pointage> findByCollaborateur_ManagerId(Long managerId);

    List<Pointage> findByCollaborateur_Matricule(String matricule);

    List<Pointage> findByCollaborateur_MatriculeAndDate(String matricule, LocalDate date);

    List<Pointage> findByCollaborateur_ManagerIdAndDate(Long managerId, LocalDate date);

    List<Pointage> findByDateAndCollaborateur_ManagerId(LocalDate date, Long managerId);




}
