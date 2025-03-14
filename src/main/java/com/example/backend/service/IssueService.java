package com.example.backend.service;

import com.example.backend.dto.IssueRequestDto;
import com.example.backend.entity.Issue;
import com.example.backend.entity.Project;
import com.example.backend.entity.Status;
import com.example.backend.entity.User;
import com.example.backend.repository.IssueRepository;
import com.example.backend.repository.ProjectRepository;
import com.example.backend.repository.StatusRepository;
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

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ProjectRepository projectRepository;


    public List<Issue> getIssuesByManagerId(Long managerId) {
        return issueRepository.findByCollaborateur_ManagerId(managerId);
    }

    public Issue createIssue(IssueRequestDto request) {
        if (request.getCollaborateur_id() == null) {
            throw new IllegalArgumentException("L'ID du collaborateur est obligatoire.");
        }

        User collaborateur = userRepository.findById(request.getCollaborateur_id())
                .orElseThrow(() -> new IllegalArgumentException("Collaborateur introuvable avec l'ID : "+ request.getCollaborateur_id()));

        Status status = statusRepository.findById(request.getStatus_id())
                .orElseThrow(() -> new IllegalArgumentException("status introuvable avec l'ID : "+ request.getStatus_id()));

        Project project = projectRepository.findById(request.getProject_id())
                .orElseThrow(() -> new IllegalArgumentException("project introuvable avec l'ID : "+ request.getProject_id()));

        if (request.getDate_debut() != null && request.getDate_echeance() != null
                && request.getDate_debut().isAfter(request.getDate_echeance())) {
            throw new IllegalArgumentException("La date de début doit être avant la date d'échéance.");
        }
        if (request.getDate_debut() != null && request.getDate_fin() != null
                && request.getDate_debut().isAfter(request.getDate_fin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date fin.");
        }


        Issue issue = new Issue();

        issue.setSujet(request.getTitre());
        issue.setDate_debut(request.getDate_debut());
        issue.setDate_fin(request.getDate_fin());
        issue.setDate_echeance(request.getDate_echeance());
        issue.setType(request.getType());
        issue.setCollaborateur(collaborateur);
        issue.setStatus(status);
        issue.setProject(project);


        return issueRepository.save(issue);
    }

    public Optional<Issue> getIssueById(Long id){
       return issueRepository.findById(id);
    }

    public List<Issue> getIssueByCollaborateurId(Long id){
        return issueRepository.findByCollaborateur_Id(id);
    }

}

