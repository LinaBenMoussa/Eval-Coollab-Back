package com.example.backend.controller;

import com.example.backend.entity.Issue;
import com.example.backend.entity.Notification;
import com.example.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/collaborateur/{collaborateurId}")
    public List<Notification> getNotificationByIdCollaborateur(@PathVariable Long collaborateurId) {
        return notificationService.getNotificationByIdCollaborateur(collaborateurId);
    }

    @DeleteMapping("/{id}")
    public String deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return "Notification supprimée avec succès !";
    }

}
