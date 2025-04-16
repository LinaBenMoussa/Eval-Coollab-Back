package com.example.backend.specification;

import com.example.backend.entity.Issue;
import com.example.backend.entity.Notification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class NotificationSpecifications {
    public static Specification<Notification> hasManagerId(Long managerId) {
        return (root, query, cb) ->
                cb.equal(root.get("collaborateur").get("manager").get("id"), managerId);
    }

    public static Specification<Notification> hasCollaborateurId(Long collaborateurId) {
        return (root, query, criteriaBuilder) -> {
            if (collaborateurId == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.equal(root.get("collaborateur").get("id"), collaborateurId);
        };
    }

    public static Specification<Notification> isBetweenDate(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) ->
                cb.between(root.get("dateEnvoi"), start, end);
    }
}
