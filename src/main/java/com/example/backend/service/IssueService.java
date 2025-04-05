package com.example.backend.service;

import com.example.backend.dto.response.IssueResponseDto;
import com.example.backend.entity.Issue;
import com.example.backend.repository.IssueRepository;
import com.example.backend.repository.ProjectRepository;
import com.example.backend.repository.StatusRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.specification.IssueSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class IssueService {
    private final IssueRepository issueRepository;

    private final UserRepository userRepository;

    private final StatusRepository statusRepository;

    private final ProjectRepository projectRepository;


    public List<Issue> getIssuesByManagerId(Long managerId) {
        return issueRepository.findByCollaborateur_ManagerId(managerId);
    }

    public Optional<Issue> getIssueById(Long id){
       return issueRepository.findById(id);
    }

    public List<Issue> getIssueByCollaborateurId(Long id){
        return issueRepository.findByCollaborateur_Id(id);
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
            String status,
            int offset,
            int limit) {

        Specification<Issue> spec = Specification.where(null);

        // Filtre par manager
        if (managerId != null) {
            spec = spec.and(IssueSpecifications.hasManagerId(managerId));
        }

        // Filtre par collaborateur
        if (collaborateurId != null) {
            spec = spec.and(IssueSpecifications.hasCollaborateurId(collaborateurId));
        }

        // Filtre par status
        if (status != null && !status.isEmpty()) {
            spec = spec.and(IssueSpecifications.hasStatus(status));
        }

        // Filtres par dates
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

        // Pagination et tri
        int page = offset / limit;
        Pageable pageable = PageRequest.of(page, limit);

        Page<Issue> result = issueRepository.findAll(spec, pageable);

        return new IssueResponseDto(result.getContent(), result.getTotalElements());
    }

}

