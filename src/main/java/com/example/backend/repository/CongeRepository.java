package com.example.backend.repository;

import com.example.backend.entity.Conge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CongeRepository extends JpaRepository<Conge, Long> {
    List<Conge> findByCollaborateur_ManagerId(Long managerId);

    List<Conge> findByCollaborateur_Id(Long collaborateurId);
}
