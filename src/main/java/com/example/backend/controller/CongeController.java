package com.example.backend.controller;

import com.example.backend.dto.CongeRequestDto;
import com.example.backend.dto.PointageRequestDto;
import com.example.backend.entity.Conge;
import com.example.backend.entity.Pointage;
import com.example.backend.service.CongeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conges")
public class CongeController {
    @Autowired
    private CongeService congeService;

    @GetMapping("/manager/{managerId}")
    public List<Conge> getCongeByManager(@PathVariable Long managerId){
        return congeService.getCongesByManagerId(managerId);
    }

    @PostMapping
    public Conge createConge(@RequestBody CongeRequestDto request) {
        return congeService.createConge(request);
    }
}
