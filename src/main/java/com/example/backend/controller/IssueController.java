package com.example.backend.controller;

import com.example.backend.dto.response.IssueResponseDto;
import com.example.backend.entity.Issue;
import com.example.backend.service.IssueService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/issues")
@AllArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @GetMapping("/manager/{managerId}")
    public List<Issue> getIssuesByManagerId(@PathVariable Long managerId) {
        return issueService.getIssuesByManagerId(managerId);
    }

    @GetMapping("/collaborateur/{collaborateurId}")
    public List<Issue> getIssuesByCollaborateurId(@PathVariable Long collaborateurId) {
        return issueService.getIssueByCollaborateurId(collaborateurId);
    }

    @GetMapping("/filtre")
    public ResponseEntity<IssueResponseDto> filterIssues(
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateFin,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDateFin,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateEcheance,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDateEcheance,
            @RequestParam(required = false) Long collaborateurId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String projet,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        IssueResponseDto response = issueService.getFilteredIssues(
                managerId,
                startDateDebut, endDateDebut,
                startDateFin, endDateFin,
                startDateEcheance, endDateEcheance,
                collaborateurId,
                projet,
                status,
                offset, limit);

        return ResponseEntity.ok(response);
    }
}

