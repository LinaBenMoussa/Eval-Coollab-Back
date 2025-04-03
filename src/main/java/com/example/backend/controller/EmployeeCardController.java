package com.example.backend.controller;

import com.example.backend.dto.EmployeeCardDTO;
import com.example.backend.service.EmployeeCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/employee-cards")
public class EmployeeCardController {

    @Autowired
    private EmployeeCardService employeeCardService;

    @GetMapping
    public List<EmployeeCardDTO> getEmployeeCards(
            @RequestParam Long managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate) {
        return employeeCardService.getEmployeeCardsForManager(managerId, selectedDate);
    }
}
