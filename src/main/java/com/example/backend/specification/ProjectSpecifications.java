package com.example.backend.specification;

import com.example.backend.entity.Project;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import java.util.List;

public class ProjectSpecifications {

    public static Specification<Project> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) {
                return cb.isTrue(cb.literal(true));
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }
    public static Specification<Project> hasStatus(int status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }


    public static Specification<Project> hasIdentifier(String identifier) {
        return (root, query, cb) -> {
            if (identifier == null || identifier.isEmpty()) {
                return cb.isTrue(cb.literal(true));
            }
            return cb.equal(root.get("identifier"), identifier);
        };
    }

    public static Specification<Project> hasCreatedBy(String createdBy) {
        return (root, query, cb) -> {
            if (createdBy == null || createdBy.isEmpty()) {
                return cb.isTrue(cb.literal(true));
            }
            return cb.equal(root.get("createdBy"), createdBy);
        };
    }

    public static Specification<Project> isBetweenCreatedDates(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) ->
                cb.between(root.get("createdDate"), start, end);
    }

    public static Specification<Project> isBetweenModifiedDates(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) ->
                cb.between(root.get("lastModifiedDate"), start, end);
    }
    public static Specification<Project> hasIdIn(List<Long> ids) {
        return (root, query, builder) -> root.get("id").in(ids);
    }

}