package com.example.backend.specification;

import com.example.backend.entity.Pointage;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class PointageSpecifications {

    public static Specification<Pointage> hasManagerId(Long managerId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(
                    root.get("collaborateur").get("manager").get("id"),
                    managerId
            );
        };
    }
    public static Specification<Pointage> hasCollaborateurId(Long collaborateurId) {
        return (root, query, criteriaBuilder) -> {
            if (collaborateurId == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.equal(root.get("collaborateur").get("id"), collaborateurId);
        };
    }

    public static Specification<Pointage> isBetweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("date"), startDate, endDate);
    }
}