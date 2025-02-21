package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PointageRequestDto {

    private LocalDate date;

    private LocalTime heure_arrivee;

    private LocalTime heure_depart;

    private Long collaborateur_id;

}
