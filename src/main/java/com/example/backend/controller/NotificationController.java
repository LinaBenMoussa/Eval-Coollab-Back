package com.example.backend.controller;

import com.example.backend.dto.response.NotificationsResponseDto;
import com.example.backend.entity.Notification;
import com.example.backend.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/filtre")
    public ResponseEntity<NotificationsResponseDto> getNotificationByManagerId(
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long collaborateurId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        NotificationsResponseDto response= notificationService.getFiltredNotifications(managerId,collaborateurId,startDate,endDate,offset,limit);
        return ResponseEntity.ok(response);
    }

}
