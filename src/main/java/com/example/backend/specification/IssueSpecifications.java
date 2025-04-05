package com.example.backend.specification;

import com.example.backend.entity.Issue;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public class IssueSpecifications {

    public static Specification<Issue> hasManagerId(Long managerId) {
        return (root, query, cb) ->
                cb.equal(root.get("collaborateur").get("manager").get("id"), managerId);
    }

    public static Specification<Issue> hasCollaborateurId(Long collaborateurId) {
        return (root, query, criteriaBuilder) -> {
            if (collaborateurId == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.equal(root.get("collaborateur").get("id"), collaborateurId);
        };
    }

    public static Specification<Issue> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isEmpty()) {
                return cb.isTrue(cb.literal(true)); // pas de filtre si status est null/vide
            }
            return cb.equal(root.get("status").get("name"), status);
        };
    }

    public static Specification<Issue> isBetweenDatesDebut(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) ->
                cb.between(root.get("date_debut"), start, end);
    }

    public static Specification<Issue> isBetweenDatesFin(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) ->
                cb.between(root.get("date_fin"), start, end);
    }

    public static Specification<Issue> isBetweenDatesEcheance(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) ->
                cb.between(root.get("date_echeance"), start, end);
    }
}