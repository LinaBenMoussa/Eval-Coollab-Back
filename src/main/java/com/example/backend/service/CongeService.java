package com.example.backend.service;

import com.example.backend.dto.CongeRequestDto;
import com.example.backend.entity.Conge;
import com.example.backend.entity.User;
import com.example.backend.repository.CongeRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CongeService {
    @Autowired
    private CongeRepository congeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Conge> getCongesByManagerId(Long managerId) {
        return congeRepository.findByCollaborateur_ManagerId(managerId);
    }

    public Conge createConge(CongeRequestDto request) {
        if (request.getCollaborateur_id() == null) {
            throw new IllegalArgumentException("L'ID du collaborateur est obligatoire.");
        }

        User collaborateur = userRepository.findById(request.getCollaborateur_id())
                .orElseThrow(() -> new IllegalArgumentException("Collaborateur introuvable avec l'ID : "+ request.getCollaborateur_id()));

        // Vérification si l'heure d'arrivée est après l'heure de départ
        if (request.getDate_debut() != null && request.getDate_fin() != null &&
                request.getDate_debut().isAfter(request.getDate_fin())) {
            throw new IllegalArgumentException("L'heure d'arrivée doit être avant l'heure de départ.");
        }

        Conge conge = new Conge();
        conge.setDate_debut(request.getDate_debut());
        conge.setDate_fin(request.getDate_fin());
        conge.setDate_demande(request.getDate_demande());
        conge.setCollaborateur(collaborateur);
        conge.setCommentaire(request.getCommentaire());
        conge.setStatus(request.getStatus());
        conge.setType(request.getType());

        return congeRepository.save(conge);
    }
}
