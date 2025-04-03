package com.example.backend.repository;

import com.example.backend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>, JpaSpecificationExecutor<Feedback> {
    List<Feedback> findByCollaborateurId(Long collaborateurId);

    List<Feedback> findByManagerId(Long managerId);

    List<Feedback> findByManagerIdAndCollaborateurId(Long managerId, Long collaborateurId);


}
