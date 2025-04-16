package com.example.backend.event;

import com.example.backend.entity.Pointage;
import com.example.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class RetardEvent {
    private final User collaborateur;
}
