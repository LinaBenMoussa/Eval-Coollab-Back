package com.example.backend.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Table(name = "t_feedback")
@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commentaire;
    private LocalDateTime dateFeedback;
    private String type;
    @ManyToOne
    @JoinColumn(name = "collaborateur_id", nullable = false)
    private User collaborateur;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;
}
