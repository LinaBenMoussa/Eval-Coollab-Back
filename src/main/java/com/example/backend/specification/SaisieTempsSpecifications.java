package com.example.backend.specification;

import com.example.backend.entity.Issue;
import com.example.backend.entity.SaisieTemps;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
public class SaisieTempsSpecifications {

    public static Specification<SaisieTemps> hasManagerId(Long managerId) {
        return (root, query, criteriaBuilder) -> {
            if (managerId == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.equal(root.get("collaborateur").get("manager").get("id"), managerId);
        };
    }

    public static Specification<SaisieTemps> hasIssue(Long issue) {
        return (root, query, cb) -> {
            if (issue == null) {
                return cb.isTrue(cb.literal(true));
            }
            return cb.equal(root.get("issue").get("id"), issue);
        };
    }

    public static Specification<SaisieTemps> isBetweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null || endDate == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Pas de filtre
            }
            return criteriaBuilder.between(root.get("date"), startDate, endDate);
        };
    }

    public static Specification<SaisieTemps> hasCollaborateurId(Long collaborateurId) {
        return (root, query, criteriaBuilder) -> {
            if (collaborateurId == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.equal(root.get("collaborateur").get("id"), collaborateurId);
        };
    }
}