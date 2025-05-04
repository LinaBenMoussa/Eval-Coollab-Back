package com.example.backend.service;

import com.example.backend.entity.JoursFeries;
import com.example.backend.repository.JoursFeriesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class JoursFeriesService {
    private final JoursFeriesRepository joursFeriesRepository;

    // Vérifie si une date est un jour férié
    public boolean estJourFerie(LocalDate date) {
        return joursFeriesRepository.findByDate(date) != null;
    }

    // Create
    public JoursFeries ajouterJourFerie(JoursFeries jourFerie) {
        return joursFeriesRepository.save(jourFerie);
    }

    // Read - tous les jours fériés
    public List<JoursFeries> getAllJoursFeries() {
        return joursFeriesRepository.findAll();
    }

    // Read - par ID
    public Optional<JoursFeries> getJourFerieById(Long id) {
        return joursFeriesRepository.findById(id);
    }

    // Update
    public JoursFeries updateJourFerie(Long id, JoursFeries updatedJourFerie) {
        return joursFeriesRepository.findById(id)
                .map(jf -> {
                    jf.setDate(updatedJourFerie.getDate());
                    jf.setNom(updatedJourFerie.getNom());
                    return joursFeriesRepository.save(jf);
                })
                .orElseThrow(() -> new RuntimeException("Jour férié non trouvé avec l'ID: " + id));
    }

    // Delete
    public void deleteJourFerie(Long id) {
        joursFeriesRepository.deleteById(id);
    }
}
