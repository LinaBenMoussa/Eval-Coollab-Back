package com.example.backend.controller;

import com.example.backend.entity.Notification;
import com.example.backend.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/collaborateur/{collaborateurId}")
    public List<Notification> getNotificationByIdCollaborateur(@PathVariable Long collaborateurId) {
        return notificationService.getNotificationByIdCollaborateur(collaborateurId);
    }

    @DeleteMapping("/{id}")
    public String deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return "Notification supprimée avec succès !";
    }

    @PutMapping("/lu")
    public String markNotificationsAsRead(@RequestBody List<Long> notificationIds) {
        notificationService.markNotificationsAsRead(notificationIds);
        return "Notifications marquées comme lues avec succès !";
    }


}
