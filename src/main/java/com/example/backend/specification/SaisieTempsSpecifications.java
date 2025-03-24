package com.example.backend.specification;

import com.example.backend.entity.SaisieTemps;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class SaisieTempsSpecifications {

    // Filtre par managerId
    public static Specification<SaisieTemps> hasManagerId(Long managerId) {
        return (root, query, criteriaBuilder) -> {
            if (managerId == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Pas de filtre
            }
            return criteriaBuilder.equal(root.get("collaborateur").get("manager").get("id"), managerId);
        };
    }

    // Filtre par plage de dates
    public static Specification<SaisieTemps> isBetweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null || endDate == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Pas de filtre
            }
            return criteriaBuilder.between(root.get("date"), startDate, endDate);
        };
    }

    // Filtre par collaborateurId
    public static Specification<SaisieTemps> hasCollaborateurId(Long collaborateurId) {
        return (root, query, criteriaBuilder) -> {
            if (collaborateurId == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Pas de filtre
            }
            return criteriaBuilder.equal(root.get("collaborateur").get("id"), collaborateurId);
        };
    }
}