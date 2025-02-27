package com.example.backend.controller;

import com.example.backend.dto.SaisieTempsRequestDto;
import com.example.backend.entity.Feedback;
import com.example.backend.entity.SaisieTemps;
import com.example.backend.service.SaisieTempsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/saisietemps")
public class SaisieTempsController {
    @Autowired
    private SaisieTempsService saisieTempsService;

    @GetMapping("/issue/{id}")
    public List<SaisieTemps> getSaisieByIssue(@PathVariable Long id){
        return saisieTempsService.getSaisieByIssueId(id);
    }

    @GetMapping("/manager/{id}")
    public List<SaisieTemps> getSaisieByManager(@PathVariable Long id){
        return saisieTempsService.getSaisieByManagerId(id);
    }

    @PostMapping
    public SaisieTemps createSaisieTemps(@RequestBody SaisieTempsRequestDto request){
        return saisieTempsService.createSaisieTemps(request);
    }
}
