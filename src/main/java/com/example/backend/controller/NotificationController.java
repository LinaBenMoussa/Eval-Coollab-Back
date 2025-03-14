package com.example.backend.controller;

import com.example.backend.entity.Issue;
import com.example.backend.entity.Notification;
import com.example.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/collaborateur/{collaborateurId}")
    public List<Notification> getIssuesByCollaborateurId(@PathVariable Long collaborateurId) {
        return notificationService.getNotificationByIdCollaborateur(collaborateurId);
    }

}
