package com.example.backend.dto;

import lombok.*;

@Data
@Getter
@Setter
public class FeedbackRequestDto {
    private String commentaire;
    private Long collaborateurId;
    private Long managerId;
    private String type;
}
