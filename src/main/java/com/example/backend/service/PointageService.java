package com.example.backend.service;

import com.example.backend.dto.NotificationRequestDto;
import com.example.backend.dto.PointageRequestDto;
import com.example.backend.dto.PointageResponseDto;
import com.example.backend.entity.Conge;
import com.example.backend.entity.Pointage;
import com.example.backend.entity.User;
import com.example.backend.repository.PointageRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.specification.PointageSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class PointageService {

    @Autowired
    private PointageRepository pointageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BitrixNotificationService bitrixNotificationService;

    @Autowired
    private EmailService emailService;

    private static final int REQUIRED_HOURS = 9; // Nombre d'heures de travail requises par jour

    public void checkWorkHoursForAllCollaborateurs(LocalDate date) {
        List<User> collaborateurs = userRepository.findByRole("Collaborateur");

        for (User collaborateur : collaborateurs) {
            List<Pointage> pointages = pointageRepository.findByCollaborateur_MatriculeAndDate(collaborateur.getMatricule(),date);

            long totalHoursWorked = 0;
            for (Pointage pointage : pointages) {
                LocalTime heureArrivee = pointage.getHeure_arrivee();
                LocalTime heureDepart = pointage.getHeure_depart();

                if (heureArrivee != null && heureDepart != null) {
                    Duration duration = Duration.between(heureArrivee, heureDepart);
                    totalHoursWorked += duration.toHours();
                }
            }

            // Vérifier si les heures travaillées sont inférieures aux heures requises
            if (totalHoursWorked < REQUIRED_HOURS) {
                String message = String.format(
                        "Vous n'avez pas atteint vos heures quotidiennes pour le %s. Heures travaillées : %d",
                        date, totalHoursWorked
                );

                // Envoyer une notification Bitrix24
                bitrixNotificationService.sendNotification(collaborateur.getId_bitrix24(), message);

                String email = collaborateur.getEmail();
                String subject = "Alerte : Heures de travail non accomplies";
                emailService.sendEmail(email, subject, message);

                LocalDateTime dateEnvoi = LocalDateTime.of(date, LocalTime.of(17, 30));
                notificationService.createNotification(new NotificationRequestDto(subject, message, collaborateur.getId(), dateEnvoi));            }
        }
    }

    public List<Pointage> getPointagesByManagerId(Long managerId) {
        return pointageRepository.findByCollaborateur_ManagerId(managerId);
    }

    public List<Pointage> getPointagesByCollaborateurId(String matricule) {
        return pointageRepository.findByCollaborateur_Matricule(matricule);
    }



    public PointageResponseDto getPointagesByManagerId(
            Long managerId,
            LocalDate startDate,
            LocalDate endDate,
            int offset,
            int limit
    ) {
        // Créez une spécification pour les filtres
        Specification<Pointage> spec = Specification.where(PointageSpecifications.hasManagerId(managerId));

        // Ajoutez le filtre par plage de dates si les dates sont fournies
        if (startDate != null && endDate != null) {
            spec = spec.and(PointageSpecifications.isBetweenDates(startDate, endDate));
        }

        // Calculez le numéro de page à partir de l'offset et de la limite
        int page = offset / limit;

        // Appliquez la pagination
        Pageable pageable = PageRequest.of(page, limit);

        // Exécutez la requête avec les filtres et la pagination
        Page<Pointage> result = pointageRepository.findAll(spec, pageable);

        // Retournez la réponse avec la liste des pointages et le nombre total
        return new PointageResponseDto(result.getContent(), result.getTotalElements());
    }

    public Pointage createPointage(PointageRequestDto request) {
        if (request.getCollaborateur_id() == null) {
            throw new IllegalArgumentException("L'ID du collaborateur est obligatoire.");
        }

        User collaborateur = userRepository.findById(request.getCollaborateur_id())
                .orElseThrow(() -> new IllegalArgumentException("Collaborateur introuvable avec l'ID : "+ request.getCollaborateur_id()));

        // Vérification si l'heure d'arrivée est après l'heure de départ
        if (request.getHeure_arrivee() != null && request.getHeure_depart() != null &&
                request.getHeure_arrivee().isAfter(request.getHeure_depart())) {
            throw new IllegalArgumentException("L'heure d'arrivée doit être avant l'heure de départ.");
        }

        Pointage pointage = new Pointage();
        pointage.setDate(request.getDate());
        pointage.setHeure_arrivee(request.getHeure_arrivee());
        pointage.setHeure_depart(request.getHeure_depart());
        pointage.setCollaborateur(collaborateur);

        // Définir le statut
        if (request.getHeure_arrivee() != null && request.getHeure_depart() == null) {
            pointage.setStatus("En poste");
        } else if (request.getHeure_arrivee() != null && request.getHeure_depart() != null) {
            pointage.setStatus("A quité");
        }

        return pointageRepository.save(pointage);
    }

    public Pointage updatePointage(Long pointageId, PointageRequestDto request) {
        // Vérification de l'existence du pointage
        Pointage pointageExist = pointageRepository.findById(pointageId)
                .orElseThrow(() -> new IllegalArgumentException("Pointage introuvable avec l'ID : " + pointageId));

        // Vérification si l'heure d'arrivée est après l'heure de départ
        if (request.getHeure_arrivee() != null && request.getHeure_depart() != null &&
                request.getHeure_arrivee().isAfter(request.getHeure_depart())) {
            throw new IllegalArgumentException("L'heure d'arrivée doit être avant l'heure de départ.");
        }

        // Mise à jour des informations du pointage
        pointageExist.setDate(request.getDate());
        pointageExist.setHeure_arrivee(request.getHeure_arrivee());
        pointageExist.setHeure_depart(request.getHeure_depart());

        // Définir le statut en fonction des nouvelles heures
        if (request.getHeure_arrivee() != null && request.getHeure_depart() == null) {
            pointageExist.setStatus("En poste");
        } else if (request.getHeure_arrivee() != null && request.getHeure_depart() != null) {
            pointageExist.setStatus("A quité");
        }

        // Sauvegarder le pointage mis à jour
        return pointageRepository.save(pointageExist);
    }

    public List<Pointage> getPointagesByManagerAndDate(Long managerId, LocalDate date) {
        return pointageRepository.findByCollaborateur_ManagerIdAndDate(managerId, date);
    }

}
