package com.example.backend.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "t_feedback")
@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String commentaire;

    private LocalDateTime date_feedback;

    private String type;

    @ManyToOne
    @JoinColumn(name = "collaborateur_id", nullable = false)
    private User collaborateur;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @CreatedDate
    private LocalDateTime createdDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @LastModifiedBy
    private String lastModifiedBy;
}
