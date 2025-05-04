package com.example.backend.controller;

import com.example.backend.service.CollaborateurStatsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/stats")
public class StatsCollaborateurController {
    private final CollaborateurStatsService collaborateurStatsService;
    @GetMapping("/{collaborateurId}")
    public ResponseEntity<Map<String,Double>> getStatsForCollaborateur(
            @PathVariable Long collaborateurId,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {

        return ResponseEntity.ok(collaborateurStatsService.getStatsForCollaborateur(collaborateurId, startDate, endDate));
    }
}
