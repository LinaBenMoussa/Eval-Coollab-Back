package com.example.backend.service;

import com.example.backend.dto.ResponseSaisieTempsDto;
import com.example.backend.dto.SaisieTempsRequestDto;
import com.example.backend.entity.Issue;
import com.example.backend.entity.SaisieTemps;
import com.example.backend.entity.User;
import com.example.backend.repository.SaisieTempsRepository;
import com.example.backend.specification.SaisieTempsSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SaisieTempsService {

    @Autowired
    private SaisieTempsRepository saisieTempsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private IssueService issueService;

    public List<SaisieTemps> getSaisieByIssueId(Long issueId) {
        return saisieTempsRepository.findByIssueId(issueId);
    }


    public List<SaisieTemps> getSaisieByManagerId(Long managerId) {
        return saisieTempsRepository.findByCollaborateur_ManagerId(managerId);
    }

    public ResponseSaisieTempsDto getSaisiesByManagerId(Long managerId, LocalDate startDate, LocalDate endDate, Long collaborateurId, int offset, int limit) {
        // Créez une spécification pour les filtres
        Specification<SaisieTemps> spec = Specification.where(SaisieTempsSpecifications.hasManagerId(managerId));

        // Ajoutez le filtre par plage de dates si les dates sont fournies
        if (startDate != null && endDate != null) {
            spec = spec.and(SaisieTempsSpecifications.isBetweenDates(startDate, endDate));
        }

        // Ajoutez le filtre par collaborateurId si fourni
        if (collaborateurId != null) {
            spec = spec.and(SaisieTempsSpecifications.hasCollaborateurId(collaborateurId));
        }

        // Appliquez la pagination
        Pageable pageable = PageRequest.of(offset / limit, limit);

        // Exécutez la requête avec les filtres et la pagination
        Page<SaisieTemps> result = saisieTempsRepository.findAll(spec, pageable);

        // Retournez la réponse avec la liste des saisies et le nombre total
        return new ResponseSaisieTempsDto(result.getContent(), result.getTotalElements());
    }
}