package com.example.backend.repository;

import com.example.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCollaborateur_Id(Long collaborateurId);

    List<Notification> findByCollaborateur_IdOrderByCreatedDateDesc(Long id);

}
