package com.example.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class CongeRequestDto {

    private LocalDateTime date_debut;

    private LocalDateTime date_fin;

    private LocalDateTime date_demande;

    private String type;

    private LocalTime heureDeb;

    private LocalTime heureFin;


    private double nbrjour;

    private String matricule;
}
