package com.example.backend.service;

import com.example.backend.dto.response.CongeResponseDto;
import com.example.backend.entity.Conge;
import com.example.backend.repository.CongeRepository;
import com.example.backend.specification.CongeSpecifications;
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

@AllArgsConstructor
@Service
public class CongeService {
    private final CongeRepository congeRepository;

    public List<Conge> getCongesByManagerId(Long managerId) {
        return congeRepository.findByCollaborateur_ManagerId(managerId);
    }

    public List<Conge> getCongesByCollaborateurId(Long collaborateurId) {
        return congeRepository.findByCollaborateur_Id(collaborateurId);
    }

    public List<Conge> getCongesByManagerAndDate(Long managerId, LocalDateTime date) {
        return congeRepository.findByCollaborateur_ManagerIdAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
                managerId, date, date);
    }

    public CongeResponseDto filtreConge(
            String type,
            LocalDate startDateDebut,
            LocalDate endDateDebut,
            LocalDate startDateFin,
            LocalDate endDateFin,
            LocalDate startDateDemande,
            LocalDate endDateDemande,
            Long collaborateurId,
            Long managerId,
            int offset,
            int limit) {

        Specification<Conge> spec = Specification.where(null);

        if (collaborateurId != null) {
            spec = spec.and(CongeSpecifications.hasCollaborateurId(collaborateurId));
        }

        if (managerId != null) {
            spec = spec.and(CongeSpecifications.hasManagerId(managerId));
        }

        if (type != null && !type.isEmpty()) {
            spec = spec.and(CongeSpecifications.hasType(type));
        }

        // Conversion des LocalDate en LocalDateTime pour les plages
        if (startDateDebut != null && endDateDebut != null) {
            spec = spec.and(CongeSpecifications.isBetweenDatesDebut(startDateDebut, endDateDebut));
        }
        if (startDateFin != null && endDateFin != null) {
            spec = spec.and(CongeSpecifications.isBetweenDatesFin(startDateFin, endDateFin));
        }
        if (startDateDemande != null && endDateDemande != null) {
            spec = spec.and(CongeSpecifications.isBetweenDatesDemande(startDateDemande, endDateDemande));
        }

        // Pagination
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "dateDebut"));
        Page<Conge> result = congeRepository.findAll(spec, pageable);

        return new CongeResponseDto(result.getContent(), result.getTotalElements());
    }

}
