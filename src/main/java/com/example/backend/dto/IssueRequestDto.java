package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class IssueRequestDto {
    private String titre;

    private LocalDateTime date_debut;

    private LocalDateTime date_echeance;

    private LocalDateTime date_fin;

    private String status;

    private String type;

    private Long collaborateur_id;
}
