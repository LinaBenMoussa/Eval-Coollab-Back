package com.example.backend.dto;

import com.example.backend.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class NotificationRequestDto {

    private String sujet;

    private String contenu;

    private Long collaborateur_id;

    private LocalDateTime dateEnvoi;
}
