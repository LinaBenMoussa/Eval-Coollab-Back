package com.example.backend.listener;

import com.example.backend.event.IssueExpiredEvent;
import com.example.backend.event.RetardEvent;
import com.example.backend.service.BitrixNotificationService;
import com.example.backend.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RetardEventListener {

    private final BitrixNotificationService bitrixNotificationService;
    private final EmailService emailService;

    @EventListener
    public void handleRetard(RetardEvent event) {
        if (event.getCollaborateur().getId_bitrix24() != null) {
            bitrixNotificationService.sendNotification(event.getCollaborateur().getId_bitrix24(), "vous êtes en retard aujourd'hui ");
        }

        String email = event.getCollaborateur().getEmail();
        if (email != null) {
            String subject = "Rappel : retard";
            emailService.sendEmail("lina.b.moussa@gmail.com", subject, "vous êtes en retard aujourd'hui");
        }
    }
}
