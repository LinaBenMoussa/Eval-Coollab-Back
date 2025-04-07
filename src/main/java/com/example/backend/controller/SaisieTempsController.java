package com.example.backend.controller;

import com.example.backend.dto.response.ResponseSaisieTempsDto;
import com.example.backend.entity.SaisieTemps;
import com.example.backend.service.SaisieTempsService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/saisietemps")
@AllArgsConstructor
public class SaisieTempsController {
    private final SaisieTempsService saisieTempsService;

    @GetMapping("/issue/{id}")
    public List<SaisieTemps> getSaisieByIssue(@PathVariable Long id){
        return saisieTempsService.getSaisieByIssueId(id);
    }

    @GetMapping("/manager/{id}")
    public List<SaisieTemps> getSaisieByManager(@PathVariable Long id){
        return saisieTempsService.getSaisieByManagerId(id);
    }

    @GetMapping("/filtre")
    public ResponseSaisieTempsDto getSaisies(
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long collaborateurId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return saisieTempsService.getSaisiesByManagerId(managerId, startDate, endDate, collaborateurId, offset, limit);
    }
}
