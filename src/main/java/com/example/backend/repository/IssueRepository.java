package com.example.backend.repository;

import com.example.backend.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByCollaborateur_ManagerId(Long managerId);

    List<Issue> findByCollaborateur_Id(Long collaborateurId);

}

