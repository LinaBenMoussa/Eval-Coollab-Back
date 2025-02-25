package com.example.backend.repository;

import com.example.backend.entity.Conge;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CongeRepository extends JpaRepository<Conge, Long> {
    List<Conge> findByCollaborateur_ManagerId(Long managerId);
}
