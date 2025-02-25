package com.example.backend.repository;

import com.example.backend.entity.SaisieTemps;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaisieTempsRepository extends JpaRepository<SaisieTemps, Long> {
    List<SaisieTemps> findByIssueId(Long issueId);
}
