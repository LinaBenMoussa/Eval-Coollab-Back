package com.example.backend.service;

import com.example.backend.dto.IssueRequestDto;
import com.example.backend.entity.Issue;
import com.example.backend.entity.User;
import com.example.backend.repository.IssueRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IssueService {
    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;



    public List<Issue> getIssuesByManagerId(Long managerId) {
        return issueRepository.findByCollaborateur_ManagerId(managerId);
    }

    public Issue createIssue(IssueRequestDto request) {
        if (request.getCollaborateur_id() == null) {
            throw new IllegalArgumentException("L'ID du collaborateur est obligatoire.");
        }

        User collaborateur = userRepository.findById(request.getCollaborateur_id())
                .orElseThrow(() -> new IllegalArgumentException("Collaborateur introuvable avec l'ID : "+ request.getCollaborateur_id()));


        if (request.getDate_debut() != null && request.getDate_echeance() != null
                && request.getDate_debut().isAfter(request.getDate_echeance())) {
            throw new IllegalArgumentException("La date de début doit être avant la date d'échéance.");
        }
        if (request.getDate_debut() != null && request.getDate_fin() != null
                && request.getDate_debut().isAfter(request.getDate_fin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date fin.");
        }


        Issue issue = new Issue();

        issue.setTitre(request.getTitre());
        issue.setDate_debut(request.getDate_debut());
        issue.setDate_fin(request.getDate_fin());
        issue.setDate_echeance(request.getDate_echeance());
        issue.setType(request.getType());
        issue.setCollaborateur(collaborateur);
        if (request.getDate_debut() != null && LocalDateTime.now().isBefore(request.getDate_debut())) {
            issue.setStatus("A faire");
        }else{
        if (request.getDate_fin() != null) {
                issue.setStatus("Terminé");
        } else {
            if (request.getDate_echeance() != null && request.getDate_echeance().isBefore(LocalDateTime.now())) {
                issue.setStatus("En retard");
            } else {
                issue.setStatus("En cours");
            }
        }}

        return issueRepository.save(issue);
    }

    public Optional<Issue> getIssueById(Long id){
       return issueRepository.findById(id);
    }

    public List<Issue> getIssueByCollaborateurId(Long id){
        return issueRepository.findByCollaborateur_Id(id);
    }

}

