package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class SaisieTempsRequestDto {

    private LocalDate date;

    private double heures;

    private String activite;

    private String commentaire;

    private Long issue_id;

    private Long collaborateur_id;
}
