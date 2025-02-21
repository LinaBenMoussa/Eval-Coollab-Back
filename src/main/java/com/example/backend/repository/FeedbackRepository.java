package com.example.backend.repository;

import com.example.backend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByCollaborateurId(Long collaborateurId);

    List<Feedback> findByManagerId(Long managerId);

    List<Feedback> findByManagerIdAndCollaborateurId(Long managerId, Long collaborateurId);


}
