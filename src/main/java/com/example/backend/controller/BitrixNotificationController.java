package com.example.backend.controller;

import com.example.backend.service.BitrixNotificationService;
import com.example.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class BitrixNotificationController {

    @Autowired
    private BitrixNotificationService bitrixNotificationService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public String sendNotification(
            @RequestParam Long userId,
            @RequestParam String message,
            @RequestParam String email) {

        try {
            // 1️⃣ Envoyer la notification Bitrix24
            bitrixNotificationService.sendNotification(userId, message);

            // 2️⃣ Envoyer l'e-mail
            String subject = "Nouvelle Notification Bitrix24";
            emailService.sendEmail(email, subject, message);

            return "Notification et e-mail envoyés avec succès !";
        } catch (Exception e) {
            return "Erreur lors de l'envoi : " + e.getMessage();
        }
    }
}
