package com.example.backend.dto.response;

import com.example.backend.entity.Pointage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class PointageResponseDto {
    private List<Pointage> pointages;
    private long total;
}
