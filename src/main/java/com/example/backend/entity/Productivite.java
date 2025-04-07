package com.example.backend.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Table(name = "t_productivite")
@Entity
public class Productivite {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate mois;

    private int rang;

    private int score;

    @ManyToOne
    @JoinColumn(name = "collaborateur_id")
    private User collaborateur;
}
