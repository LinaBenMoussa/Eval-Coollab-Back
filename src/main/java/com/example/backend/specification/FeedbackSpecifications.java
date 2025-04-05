package com.example.backend.specification;

import com.example.backend.entity.Feedback;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class FeedbackSpecifications {

    public static Specification<Feedback> hasCollaborateurId(Long collaborateurId) {
        return (root, query, cb) ->
                cb.equal(root.get("collaborateur").get("id"), collaborateurId);
    }

    public static Specification<Feedback> hasManagerId(Long managerId) {
        return (root, query, cb) ->
                cb.equal(root.get("manager").get("id"), managerId);
    }

    public static Specification<Feedback> hasType(String type) {
        return (root, query, cb) ->
                cb.equal(root.get("type"), type);
    }

    public static Specification<Feedback> isBetweenDatesFeedback(LocalDate start, LocalDate end) {
        return (root, query, cb) ->
                cb.between(root.get("date_feedback"),
                        start.atStartOfDay(),
                        end.atTime(23, 59, 59));
    }
}
