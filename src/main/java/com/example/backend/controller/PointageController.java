package com.example.backend.controller;

import com.example.backend.dto.PointageRequestDto;
import com.example.backend.dto.PointageResponseDto;
import com.example.backend.entity.Pointage;
import com.example.backend.service.PointageService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pointages")
@AllArgsConstructor
public class PointageController {
    private final PointageService pointageService;

    @GetMapping("/manager/{managerId}")
    public List<Pointage> getPointageByManager(@PathVariable Long managerId){
       return pointageService.getPointagesByManagerId(managerId);
    }

    @GetMapping("/collaborateur/{collaborateurId}")
    public List<Pointage> getPointageByCollaborateur(@PathVariable String matricule){
        return pointageService.getPointagesByCollaborateurId(matricule);
    }

    @PostMapping
    public Pointage createPointage(@RequestBody PointageRequestDto request) {
        return pointageService.createPointage(request);
    }

    @PostMapping("/check-all")
    public String checkWorkHoursForAllCollaborateurs(@RequestParam String date) { // La date au format "yyyy-MM-dd"
        LocalDate localDate = LocalDate.parse(date);
        pointageService.checkWorkHoursForAllCollaborateurs(localDate);
        return "Vérification des heures de travail pour tous les collaborateurs terminée.";
    }

    @GetMapping("/manager/{managerId}/{date}")
    public List<Pointage> getPointagesByManagerAndDate(@PathVariable Long managerId, @PathVariable LocalDate date){
        return pointageService.getPointagesByManagerAndDate(managerId, date);
    }

    @GetMapping("/filtre")
    public PointageResponseDto getPointages(
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long collaborateurId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit
    ) {
        return pointageService.getPointagesByManagerId(managerId, startDate, endDate, collaborateurId, offset, limit);
    }
}
