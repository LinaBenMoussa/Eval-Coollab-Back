package com.example.backend.controller;

import com.example.backend.dto.response.ProjectResponseDto;
import com.example.backend.service.ProjetService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@AllArgsConstructor
@RestController
@RequestMapping("/projets")
public class ProjectController {

    private final ProjetService projectService;

    @GetMapping("/filtre")
    public ResponseEntity<ProjectResponseDto> getFilteredProjects(
            @RequestParam(required = false) Long collaborateurId,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String identifier,
            @RequestParam(required = false) Integer status,

            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        ProjectResponseDto response = projectService.getFilteredProjects(
                managerId,
                collaborateurId,
                name,
                identifier,
                status,
                offset,
                limit);

        return ResponseEntity.ok(response);
    }
}