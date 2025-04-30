package com.example.backend.service;

import com.example.backend.dto.response.ProjectResponseDto;
import com.example.backend.dto.response.ProjectWithIssueCountDto;
import com.example.backend.entity.Project;
import com.example.backend.repository.ProjectRepository;
import com.example.backend.specification.ProjectSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class ProjetService {

    private final IssueService issueService;
    private final ProjectRepository projectRepository;

    public ProjectResponseDto getFilteredProjects(
            Long managerId,
            Long collaborateurId,
            String name,
            String identifier,
            Integer status,
            int offset,
            int limit) {

        Specification<Project> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(ProjectSpecifications.hasName(name));
        }

        if (status != null) {
            spec = spec.and(ProjectSpecifications.hasStatus(status));
        }

        if (identifier != null && !identifier.isEmpty()) {
            spec = spec.and(ProjectSpecifications.hasIdentifier(identifier));
        }

        Set<Long> idsProjects = new HashSet<>();

        if (collaborateurId != null) {
            List<Long> collaborateurProjects = issueService.getIssueByCollaborateurId(collaborateurId)
                    .stream()
                    .map(issue -> issue.getProject().getId())
                    .toList();
            idsProjects.addAll(collaborateurProjects);
        } else if (managerId != null) {
            List<Long> managerProjects = issueService.getIssuesByManagerId(managerId)
                    .stream()
                    .map(issue -> issue.getProject().getId())
                    .toList();
            idsProjects.addAll(managerProjects);
        }

        if (!idsProjects.isEmpty()) {
            spec = spec.and(ProjectSpecifications.hasIdIn(new ArrayList<>(idsProjects)));
        }

        int page = offset / limit;
        Pageable pageable = PageRequest.of(page, limit);

        Page<Project> result = projectRepository.findAll(spec, pageable);

        // Map vers des DTOs avec le nombre d'issues
        List<ProjectWithIssueCountDto> projectDtos = result.getContent().stream()
                .map(project -> {
                    long issueCount = issueService.countByProjectId(project.getId());
                    return new ProjectWithIssueCountDto(
                            project.getId(),
                            project.getName(),
                            project.getIdentifier(),
                            project.getStatus(),
                            issueCount
                    );
                })
                .toList();

        return new ProjectResponseDto(projectDtos, result.getTotalElements());
    }
}
