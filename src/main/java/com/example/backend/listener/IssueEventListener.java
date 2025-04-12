package com.example.backend.listener;

import com.example.backend.event.IssueExpiredEvent;
import com.example.backend.service.BitrixNotificationService;
import com.example.backend.service.EmailService;
import com.example.backend.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IssueEventListener {

    private final BitrixNotificationService bitrixNotificationService;
    private final EmailService emailService;

    @EventListener
    public void handleIssueExpired(IssueExpiredEvent event) {
        System.out.println("⚠️ L'issue avec ID " + event.getIssue().getId() +
                " a dépassé sa date d’échéance : " + event.getIssue().getDate_echeance());
        if (event.getIssue().getCollaborateur().getId_bitrix24() != null) {
            bitrixNotificationService.sendNotification(event.getIssue().getCollaborateur().getId_bitrix24(), "vous dépassez la date d’échéance de votre tache "+event.getIssue().getSujet());
        }

        String email = event.getIssue().getCollaborateur().getEmail();
        if (email != null) {
            String subject = "Rappel : Tache en retard";
            emailService.sendEmail("lina.b.moussa@gmail.com", subject, "vous dépassez la date d’échéance de votre tache "+event.getIssue().getSujet());
        }
    }
}

