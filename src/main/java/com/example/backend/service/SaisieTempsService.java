package com.example.backend.service;

import com.example.backend.dto.SaisieTempsRequestDto;
import com.example.backend.entity.Issue;
import com.example.backend.entity.SaisieTemps;
import com.example.backend.entity.User;
import com.example.backend.repository.SaisieTempsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public SaisieTemps createSaisieTemps(SaisieTempsRequestDto request) {

        User collaborateur = userService.getUserById(request.getCollaborateur_id())
                .orElseThrow(() -> new RuntimeException("Collaborateur introuvable !"));
        Issue issue = issueService.getIssueById(request.getIssue_id())
                .orElseThrow(() -> new RuntimeException("Issue introuvable !"));


        SaisieTemps saisieTemps = new SaisieTemps();
        saisieTemps.setCommentaire(request.getCommentaire());
        saisieTemps.setCollaborateur(collaborateur);
        saisieTemps.setIssue(issue);
        saisieTemps.setDate(request.getDate());
        saisieTemps.setActivite(request.getActivite());
        saisieTemps.setHeures(request.getHeures());

        return saisieTempsRepository.save(saisieTemps);
    }


    public List<SaisieTemps> getSaisieByManagerId(Long managerId) {
        return saisieTempsRepository.findByCollaborateur_ManagerId(managerId);
    }
}