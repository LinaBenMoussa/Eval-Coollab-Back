package com.example.backend.service;

import com.example.backend.dto.response.IssueResponseDto;
import com.example.backend.dto.response.IssueWithTimeEntryCountDto;
import com.example.backend.entity.Issue;
import com.example.backend.repository.IssueRepository;
import com.example.backend.specification.IssueSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class IssueService {
    private final IssueRepository issueRepository;

    private final SaisieTempsService saisieTempsService;


    public List<Issue> getIssuesByManagerId(Long managerId) {
        return issueRepository.findByCollaborateur_ManagerId(managerId);
    }

    public List<Issue> getIssueByCollaborateurAndPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate){
       return issueRepository.findByCollaborateurAndDateDebutBetween(id,startDate,endDate);
    }

    public List<Issue> getIssueByCollaborateurId(Long id){
        return issueRepository.findByCollaborateur_Id(id);
    }
    public long countByProjectId(Long projectId) {
        return issueRepository.countByProjectId(projectId);
    }

    public IssueResponseDto getFilteredIssues(
            Long managerId,
            LocalDate startDateDebut,
            LocalDate endDateDebut,
            LocalDate startDateFin,
            LocalDate endDateFin,
            LocalDate startDateEcheance,
            LocalDate endDateEcheance,
            Long collaborateurId,
            String projet,
            String status,
            int offset,
            int limit) {

        Specification<Issue> spec = Specification.where(null);

        if (managerId != null) {
            spec = spec.and(IssueSpecifications.hasManagerId(managerId));
        }

        if (collaborateurId != null) {
            spec = spec.and(IssueSpecifications.hasCollaborateurId(collaborateurId));
        }

        if (status != null && !status.isEmpty()) {
            spec = spec.and(IssueSpecifications.hasStatus(status));
        }

        if (projet != null && !projet.isEmpty()) {
            spec = spec.and(IssueSpecifications.hasProjet(projet));
        }

        if (startDateDebut != null && endDateDebut != null) {
            spec = spec.and(IssueSpecifications.isBetweenDatesDebut(
                    startDateDebut.atStartOfDay(),
                    endDateDebut.atTime(23, 59, 59)));
        }

        if (startDateFin != null && endDateFin != null) {
            spec = spec.and(IssueSpecifications.isBetweenDatesFin(
                    startDateFin.atStartOfDay(),
                    endDateFin.atTime(23, 59, 59)));
        }

        if (startDateEcheance != null && endDateEcheance != null) {
            spec = spec.and(IssueSpecifications.isBetweenDatesEcheance(
                    startDateEcheance.atStartOfDay(),
                    endDateEcheance.atTime(23, 59, 59)));
        }

        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "dateDebut"));

        Page<Issue> result = issueRepository.findAll(spec, pageable);

        List<IssueWithTimeEntryCountDto> enrichedIssues = result.getContent().stream()
                .map(issue -> {
                    long timeCount = saisieTempsService.countByIssueId(issue.getId());
                    return new IssueWithTimeEntryCountDto(
                            issue,
                            timeCount
                    );
                })
                .toList();

        return new IssueResponseDto(enrichedIssues, result.getTotalElements());
    }
}

