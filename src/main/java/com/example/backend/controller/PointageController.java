package com.example.backend.controller;

import com.example.backend.dto.PointageRequestDto;
import com.example.backend.entity.Pointage;
import com.example.backend.service.PointageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public Pointage createPointage(@RequestBody PointageRequestDto request) {
        return pointageService.createPointage(request);
    }
}
