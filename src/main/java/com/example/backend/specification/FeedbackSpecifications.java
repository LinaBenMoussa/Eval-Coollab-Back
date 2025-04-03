package com.example.backend.specification;

import com.example.backend.entity.Feedback;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class FeedbackSpecifications {

    // Filtre par ID du collaborateur
    public static Specification<Feedback> hasCollaborateurId(Long collaborateurId) {
        return (root, query, cb) ->
                cb.equal(root.get("collaborateur").get("id"), collaborateurId);
    }

    // Filtre par ID du manager
    public static Specification<Feedback> hasManagerId(Long managerId) {
        return (root, query, cb) ->
                cb.equal(root.get("manager").get("id"), managerId);
    }

    // Filtre par type de feedback
    public static Specification<Feedback> hasType(String type) {
        return (root, query, cb) ->
                cb.equal(root.get("type"), type);
    }

    // Filtre par date de feedback entre deux dates (LocalDate converti en LocalDateTime)
    public static Specification<Feedback> isBetweenDatesFeedback(LocalDate start, LocalDate end) {
        return (root, query, cb) ->
                cb.between(root.get("date_feedback"),
                        start.atStartOfDay(),
                        end.atTime(23, 59, 59));
    }
}
