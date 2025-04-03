package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Service
public class BitrixNotificationService {

    @Value("${bitrix.webhook.url}")
    private String bitrixWebhookUrl;

    public void sendNotification(Long userId, String message) {
        RestTemplate restTemplate = new RestTemplate();

        String jsonPayload = String.format(
                "{\"USER_ID\": %d, \"MESSAGE\": \"%s\"}",
                userId, message
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

        try {
            String response = restTemplate.postForObject(bitrixWebhookUrl, request, String.class);
            System.out.println("Réponse de Bitrix : " + response);
        } catch (RestClientException e) {
            System.err.println("Erreur lors de l'envoi de la notification : " + e.getMessage());
        }
    }
}
