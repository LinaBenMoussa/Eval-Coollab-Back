package com.example.backend.controller;

import com.example.backend.service.BitrixNotificationService;
import com.example.backend.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class BitrixNotificationController {

    private final BitrixNotificationService bitrixNotificationService;

    private final EmailService emailService;

    @PostMapping("/send")
    public String sendNotification(
            @RequestParam Long userId,
            @RequestParam String message,
            @RequestParam String email) {

        try {
            bitrixNotificationService.sendNotification(userId, message);

            String subject = "Nouvelle Notification Bitrix24";
            emailService.sendEmail(email, subject, message);

            return "Notification et e-mail envoyés avec succès !";
        } catch (Exception e) {
            return "Erreur lors de l'envoi : " + e.getMessage();
        }
    }
}
