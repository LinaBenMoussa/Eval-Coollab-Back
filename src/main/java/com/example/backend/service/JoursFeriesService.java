package com.example.backend.service;

import com.example.backend.repository.JoursFeriesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@AllArgsConstructor
@Service
public class JoursFeriesService {
    private final JoursFeriesRepository joursFeriesRepository;

    public boolean estJourFerie(LocalDate date) {
        return joursFeriesRepository.findByDate(date) != null;
    }


}
