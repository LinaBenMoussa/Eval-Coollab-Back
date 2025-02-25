package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class CongeRequestDto {

    private LocalDateTime date_debut;

    private LocalDateTime date_fin;

    private LocalDateTime date_demande;

    private String type;

    private String status;

    private String commentaire;

    private Long collaborateur_id;
}
