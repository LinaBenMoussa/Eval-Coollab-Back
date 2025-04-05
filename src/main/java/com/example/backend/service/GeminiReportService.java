package com.example.backend.service;

import com.example.backend.dto.request.EmployeeDataDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiReportService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiReportService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String generateReport(EmployeeDataDto data) {
        try {
            String prompt = buildPrompt(data);

            String requestBody = buildGeminiRequest(prompt);

            ResponseEntity<String> response = callGeminiAPI(requestBody);

            return extractContent(response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport", e);
        }
    }

    private String buildPrompt(EmployeeDataDto data) {
        return String.format("""
        Génère un **rapport professionnel** d'évaluation d'un employé au **format HTML complet**.

        ✅ Contraintes :
        - Le code HTML doit être **entièrement structuré**, **stylisé** avec des couleurs sobres (bleu, gris, blanc) pour un rendu professionnel.
        - Utilise une **mise en page élégante** avec des sections bien séparées (par exemple : encadrés, tableaux ou cartes).
        - **Ne commence pas la réponse par** ```html ou '''html.
        - **Retourne uniquement le code HTML brut**, sans balise Markdown, sans commentaire, sans texte autour, sans balise <html> au debut.

        ✅ Contenu à inclure dans le rapport :

        - **Employé** : %s
        - **Période** : %s

        ### Points forts :
        - Productivité : %.2f / 100
        - Respect des échéances : %.2f%%

        ### Points à améliorer :
        - Taux de retard : %.2f%%

        ### Recommandations :
        - Rédige 3 recommandations concrètes et personnalisées pour améliorer les performances.

        ✅ Ton : professionnel, objectif, constructif.

        ✅ Objectif : ce rapport sera affiché directement dans une page web ou exporté en PDF.
        """,
                data.getEmployeeName(),
                data.getPeriod(),
                data.getProductivityScore(),
                data.getRespectEcheanceRate(),
                data.getRetardRate()
        );
    }



    private String buildGeminiRequest(String prompt) throws Exception {
        return objectMapper.writeValueAsString(
                new GeminiRequest(
                        new Content(new Part[] { new Part(prompt) })
                )
        );
    }

    private ResponseEntity<String> callGeminiAPI(String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("AIzaSyBj7pfcDFIv7IoccOSuZuDnhxchuzPjLgc", apiKey);

        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyBj7pfcDFIv7IoccOSuZuDnhxchuzPjLgc";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        return restTemplate.postForEntity(apiUrl, request, String.class);
    }

    private String extractContent(String jsonResponse) throws Exception {
        GeminiResponse response = objectMapper.readValue(jsonResponse, GeminiResponse.class);
        return response.getContent();
    }

    // Classes DTO pour la requête/réponse Gemini
    private record GeminiRequest(Content contents) {}
    private record Content(Part[] parts) {}
    private record Part(String text) {}
    private record GeminiResponse(Candidate[] candidates) {
        String getContent() {
            return candidates[0].content().parts()[0].text();
        }
    }
    private record Candidate(Content content) {}
}