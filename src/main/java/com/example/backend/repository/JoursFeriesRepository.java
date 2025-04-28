package com.example.backend.repository;

import com.example.backend.entity.JoursFeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface JoursFeriesRepository extends JpaRepository<JoursFeries, Long> {
    JoursFeries findByDate(LocalDate date);
}
