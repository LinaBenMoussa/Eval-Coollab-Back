package com.example.backend.dto.response;

import com.example.backend.entity.Conge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CongeResponseDto {
    private List<Conge> conges;
    private long total;
}