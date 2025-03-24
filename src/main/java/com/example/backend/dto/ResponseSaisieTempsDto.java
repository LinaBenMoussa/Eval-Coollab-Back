package com.example.backend.dto;

import com.example.backend.entity.SaisieTemps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ResponseSaisieTempsDto {
    private List<SaisieTemps> saisies;
    private long total;
}
