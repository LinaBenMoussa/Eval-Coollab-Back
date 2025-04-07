package com.example.backend.controller;

import com.example.backend.dto.response.EmployeeCardDTO;
import com.example.backend.service.EmployeeCardService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/employee-cards")
@AllArgsConstructor
public class EmployeeCardController {
    private final EmployeeCardService employeeCardService;

    @GetMapping
    public List<EmployeeCardDTO> getEmployeeCards(
            @RequestParam Long managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate) {
        return employeeCardService.getEmployeeCardsForManager(managerId, selectedDate);
    }
}
