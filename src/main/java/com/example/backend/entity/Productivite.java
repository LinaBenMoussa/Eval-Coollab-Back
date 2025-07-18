package com.example.backend.entity;

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

    private float score;

    private Long collaborateurId;
    private String nom;
    private String prenom;
    private String periode;
}
