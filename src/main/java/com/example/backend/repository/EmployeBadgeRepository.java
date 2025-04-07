package com.example.backend.repository;

import com.example.backend.entity.EmployeBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeBadgeRepository extends JpaRepository<EmployeBadge,Long> {
    EmployeBadge findByCollaborateur_IdAndBadge_Id(Long collaborateurId,Long badgeId);
    List<EmployeBadge> findByCollaborateur_Id(Long collaborateurId);

}
