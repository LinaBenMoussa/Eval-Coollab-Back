package com.example.backend.controller;

import com.example.backend.dto.PointageRequestDto;
import com.example.backend.entity.Pointage;
import com.example.backend.service.PointageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pointages")
public class PointageController {
    @Autowired
    private PointageService pointageService;

    @GetMapping("/manager/{managerId}")
    public List<Pointage> getPointageByManager(@PathVariable Long managerId){
       return pointageService.getPointagesByManagerId(managerId);
    }

    @GetMapping("/collaborateur/{collaborateurId}")
    public List<Pointage> getPointageByCollaborateur(@PathVariable Long collaborateurId){
        return pointageService.getPointagesByCollaborateurId(collaborateurId);
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
}
