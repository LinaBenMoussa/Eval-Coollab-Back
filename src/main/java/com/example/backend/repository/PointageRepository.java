package com.example.backend.repository;

import com.example.backend.entity.Pointage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointageRepository extends JpaRepository<Pointage, Long> {
    List<Pointage> findByCollaborateur_ManagerId(Long managerId);

}
