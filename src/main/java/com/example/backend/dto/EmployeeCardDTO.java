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
    private String congeType;
    private boolean isLate;
    private boolean completedWorkDay;
    private double requiredHours;
    private double dureeAutorisation;
    private LocalTime deb_Autorisation;
    private LocalTime fin_Autorisation;

}
