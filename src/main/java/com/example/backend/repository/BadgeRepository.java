package com.example.backend.repository;

import com.example.backend.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Badge findByName(String nomBadge);
}
