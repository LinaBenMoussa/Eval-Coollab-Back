package com.example.backend.repository;

import com.example.backend.entity.Pointage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PointageRepository extends JpaRepository<Pointage, Long> {
    List<Pointage> findByCollaborateur_ManagerId(Long managerId);

    List<Pointage> findByCollaborateur_Id(Long collaborateurId);

    List<Pointage> findByCollaborateur_IdAndDate(Long collaborateurId, LocalDate date);


}
