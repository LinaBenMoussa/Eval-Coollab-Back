package com.example.backend.repository;

import com.example.backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCollaborateur_Id(Long collaborateurId);

    List<Notification> findByCollaborateur_IdOrderByCreatedDateDesc(Long id);

    List<Notification> findByCollaborateur_ManagerIdOrderByCreatedDateDesc(Long id);

    Page<Notification> findAll(Specification<Notification> spec, Pageable pageable);
}
