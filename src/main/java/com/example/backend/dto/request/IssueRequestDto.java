package com.example.backend.dto.request;

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

    private Long status_id;

    private Long project_id;

    private String type;

    private Long collaborateur_id;
}
