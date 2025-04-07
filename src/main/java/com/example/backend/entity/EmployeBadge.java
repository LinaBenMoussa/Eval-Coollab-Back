package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Table(name = "t_employeBadge")
@Entity
public class EmployeBadge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "collaborateur_id")
    private User collaborateur;

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;

    private int nbrBadge;

    private LocalDate dateAttribution;
}

