package com.example.backend.dto.request;

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
