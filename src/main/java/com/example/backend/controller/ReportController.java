package com.example.backend.controller;

import com.example.backend.dto.request.EmployeeDataDto;
import com.example.backend.service.GeminiReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final GeminiReportService reportService;

    public ReportController(GeminiReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<String> generateReport(@RequestBody EmployeeDataDto data) {
        try {
            String report = reportService.generateReport(data);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}