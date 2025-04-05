package com.example.backend.specification;

import com.example.backend.entity.Conge;
import com.example.backend.entity.Pointage;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CongeSpecifications {

    public static Specification<Conge> hasCollaborateurId(Long collaborateurId) {
        return (root, query, criteriaBuilder) -> {
            if (collaborateurId == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.equal(root.get("collaborateur").get("id"), collaborateurId);
        };
    }

    public static Specification<Conge> hasManagerId(Long managerId) {
        return (root, query, criteriaBuilder) -> {
            // Accéder à collaborateur.manager.id via les relations
            return criteriaBuilder.equal(
                    root.get("collaborateur").get("manager").get("id"),
                    managerId
            );
        };
    }

    public static Specification<Conge> hasType(String type) {
        return (root, query, cb) ->
                cb.equal(root.get("type"), type);
    }

    public static Specification<Conge> isBetweenDatesDebut(LocalDate start, LocalDate end) {
        return (root, query, cb) ->
                cb.between(root.get("dateDebut"),
                        start.atStartOfDay(),
                        end.atTime(LocalTime.MAX));
    }

    public static Specification<Conge> isBetweenDatesFin(LocalDate start, LocalDate end) {
        return (root, query, cb) ->
                cb.between(root.get("dateFin"),
                        start.atStartOfDay(),
                        end.atTime(LocalTime.MAX));
    }

    public static Specification<Conge> isBetweenDatesDemande(LocalDate start, LocalDate end) {
        return (root, query, cb) ->
                cb.between(root.get("date_demande"),
                        start.atStartOfDay(),
                        end.atTime(LocalTime.MAX));
    }
}
