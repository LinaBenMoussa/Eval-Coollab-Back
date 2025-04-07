package com.example.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BadgeRequest {
    private LocalDate mois;
}
