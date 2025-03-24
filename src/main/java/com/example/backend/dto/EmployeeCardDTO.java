package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class EmployeeCardDTO {
    private Long id;
    private LocalDate date;
    private LocalTime heureArrivee;
    private LocalTime heureDepart;
    private String status;
    private String collaborateurNom;
    private String congeType; // Type de congé (ex: "En congé", "Autorisation")
    private boolean isLate; // Retard
    private boolean completedWorkDay; // A fait ses heures
    private double requiredHours; // Heures requises
    private double dureeAutorisation;
}
