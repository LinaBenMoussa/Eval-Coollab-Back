package com.example.backend.service;

import com.example.backend.dto.request.EmployeeDataDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    - **Retourne uniquement le code HTML brut**, sans balise Markdown, sans commentaire, sans texte autour, sans balise <html> au début.

    ✅ Très important :
    - Chaque **point fort**, **point à améliorer** et **recommandation** doit être **strictement basé sur les statistiques fournies**.
    - **Ne fais aucune supposition**. Ne génère rien qui ne peut être déduit directement des données suivantes.

    ✅ Contenu à inclure dans le rapport :

    - **Employé** : %s
    - **Période** : %s

    ### Statistiques disponibles :
    - Productivité : %s
    - Respect des échéances : %s
    - Taux de retard : %s
    - Temps moyen de réalisation des tâches : %s
    - Taux d’utilisation de congés : %s
    - Moyenne d'heures de travail par jour : %s
    - Taux de feedbacks négatifs : %s
    - Taux de feedbacks positifs : %s

    ### Points forts :
    - Liste des points forts de cet employé **basés uniquement sur les chiffres**.

    ### Points à améliorer :
    - Liste des points à améliorer **appuyés par les statistiques**.

    ### Recommandations :
    - Rédige 3 recommandations **concrètes et personnalisées**, toujours **en lien direct avec les statistiques** ci-dessus.

    ✅ Ton : professionnel, objectif, constructif.

    ✅ Objectif : ce rapport sera affiché directement dans une page web ou exporté en PDF.
    """,
                data.getEmployeeName(),
                data.getPeriod(),
                data.getProductivityScore(),
                data.getRespectEcheanceRate(),
                data.getRetardRate(),
                data.getAverageTaskCompletionTime(),
                data.getCongeUtilizationRate(),
                data.getDailyAvgWorkingHours(),
                data.getNegativeRate(),
                data.getPositiveRate(),
                data.getOvertimeHours(),
                data.getTotalHoursMissing()
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