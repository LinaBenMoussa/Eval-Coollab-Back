package com.example.backend.controller;

import com.example.backend.entity.JoursFeries;
import com.example.backend.service.JoursFeriesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/jours-feries")
public class JoursFeriesController {

    private final JoursFeriesService joursFeriesService;

    // GET - Récupérer tous les jours fériés
    @GetMapping
    public List<JoursFeries> getAllJoursFeries() {
        return joursFeriesService.getAllJoursFeries();
    }

    // GET - Récupérer un jour férié par ID
    @GetMapping("/{id}")
    public ResponseEntity<JoursFeries> getJourFerieById(@PathVariable Long id) {
        Optional<JoursFeries> jourFerie = joursFeriesService.getJourFerieById(id);
        return jourFerie.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Ajouter un jour férié
    @PostMapping
    public JoursFeries ajouterJourFerie(@RequestBody JoursFeries jourFerie) {
        return joursFeriesService.ajouterJourFerie(jourFerie);
    }

    // PUT - Modifier un jour férié
    @PutMapping("/{id}")
    public ResponseEntity<JoursFeries> updateJourFerie(@PathVariable Long id, @RequestBody JoursFeries updatedJourFerie) {
        try {
            JoursFeries jourFerie = joursFeriesService.updateJourFerie(id, updatedJourFerie);
            return ResponseEntity.ok(jourFerie);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Supprimer un jour férié
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJourFerie(@PathVariable Long id) {
        joursFeriesService.deleteJourFerie(id);
        return ResponseEntity.noContent().build();
    }

}
