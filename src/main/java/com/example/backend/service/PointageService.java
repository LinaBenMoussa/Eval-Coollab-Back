package com.example.backend.service;

import com.example.backend.dto.request.NotificationRequestDto;
import com.example.backend.dto.request.PointageRequestDto;
import com.example.backend.dto.response.PointageResponseDto;
import com.example.backend.entity.Conge;
import com.example.backend.entity.Pointage;
import com.example.backend.entity.RetardLog;
import com.example.backend.entity.User;
import com.example.backend.event.RetardEvent;
import com.example.backend.repository.CongeRepository;
import com.example.backend.repository.PointageRepository;
import com.example.backend.repository.RetardLogRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.specification.PointageSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class PointageService {

    private final PointageRepository pointageRepository;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    private final BitrixNotificationService bitrixNotificationService;

    private final EmailService emailService;

    private final CongeRepository congeRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final RetardLogRepository retardLogRepository;


    private static final int REQUIRED_HOURS = 9;

    public void checkWorkHoursForAllCollaborateurs(LocalDate date) {
        List<User> collaborateurs = userRepository.findByRole("Collaborateur");

        for (User collaborateur : collaborateurs) {
            List<Pointage> pointages = pointageRepository.findByCollaborateur_MatriculeAndDate(
                    collaborateur.getMatricule(), date
            );
            LocalDate selectedDate=date;
            String matricule=collaborateur.getMatricule();

            Conge conge = congeRepository.findByCollaborateurAndDateRange(matricule, selectedDate);
            long congeHours = 0;
            long autorisationHours = 0;

            if (conge != null) {
                if ("CA".equals(conge.getType())) {
                    congeHours = 9;
                } else if ("A".equals(conge.getType())) {
                    autorisationHours = Duration.between(conge.getHeureDeb(), conge.getHeureFin()).toHours();
                }
            }

            long requiredHoursAdjusted = Math.max(0, REQUIRED_HOURS - (congeHours + autorisationHours));

            long totalHoursWorked = 0;
            for (Pointage pointage : pointages) {
                LocalTime heureArrivee = pointage.getHeure_arrivee();
                LocalTime heureDepart = pointage.getHeure_depart();

                if (heureArrivee != null && heureDepart != null) {
                    Duration duration = Duration.between(heureArrivee, heureDepart);
                    totalHoursWorked += duration.toHours();
                }

            }

            if (totalHoursWorked < requiredHoursAdjusted) {
                String message = String.format(
                        "Vous n'avez pas atteint vos heures quotidiennes pour le %s. Heures travaillées : %d/%d",
                        date, totalHoursWorked, requiredHoursAdjusted
                );

                // Envoyer une notification Bitrix24
               bitrixNotificationService.sendNotification(collaborateur.getId_bitrix24(), message);

                String email = collaborateur.getEmail();
                String subject = "Alerte : Heures de travail non accomplies";
                if(email != null) {
                    emailService.sendEmail("lina.b.moussa@gmail.com", subject, message);
                }
                LocalDateTime dateEnvoi = LocalDateTime.of(LocalDate.now(), LocalTime.now());
                notificationService.createNotification(new NotificationRequestDto(subject, message, collaborateur.getId(), dateEnvoi));

            }
    }}

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
            Long collaborateurId,
            int offset,
            int limit
    ) {
        Specification<Pointage> spec = Specification.where(null);

        // Filtre par manager
        if (managerId != null) {
            spec = spec.and(PointageSpecifications.hasManagerId(managerId));
        }

        // Ajoutez le filtre par plage de dates si les dates sont fournies
        if (startDate != null && endDate != null) {
            spec = spec.and(PointageSpecifications.isBetweenDates(startDate, endDate));
        }

        if (collaborateurId != null) {
            spec = spec.and(PointageSpecifications.hasCollaborateurId(collaborateurId));
        }

        int page = offset / limit;

        Pageable pageable = PageRequest.of(page, limit);

        Page<Pointage> result = pointageRepository.findAll(spec, pageable);

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

    public void checkIsLate(LocalDate date) {
        List<User> collaborateurs = userRepository.findByRole("Collaborateur");

        for (User collaborateur : collaborateurs) {
            String matricule = collaborateur.getMatricule();

            // 🔒 Éviter les doublons : on vérifie si le collaborateur est déjà dans RetardLog pour ce jour-là
            boolean dejaSignale = retardLogRepository
                    .findByCollaborateur_MatriculeAndDateRetard(matricule, date)
                    .isPresent();

            if (dejaSignale) continue;

            List<Pointage> pointages = pointageRepository.findByCollaborateur_MatriculeAndDate(matricule, date);
            Conge conge = congeRepository.findByCollaborateurAndDateRange(matricule, date);
            LocalTime now = LocalTime.now();
            LocalTime limiteRetard = LocalTime.of(8, 10);

            // 1. Congé total : on skip
            if (conge != null && "CA".equals(conge.getType())) {
                continue;
            }

            boolean isEnRetard = false;

            // 2. Pas de pointage
            if (pointages.isEmpty()) {
                if (conge == null && now.isAfter(limiteRetard)) {
                    isEnRetard = true;
                } else if (conge != null && "A".equals(conge.getType())) {
                    LocalTime heureFinAutorisation = conge.getHeureFin();
                    if (heureFinAutorisation != null && !heureFinAutorisation.isBefore(limiteRetard)) {
                        limiteRetard = heureFinAutorisation.plusMinutes(10);
                    }
                    if (now.isAfter(limiteRetard)) {
                        isEnRetard = true;
                    }
                }
            } else {
                // 3. Avec pointage(s)
                for (Pointage pointage : pointages) {
                    LocalTime heureArrivee = pointage.getHeure_arrivee();

                    if (conge != null && "A".equals(conge.getType())) {
                        LocalTime heureFinAutorisation = conge.getHeureFin();
                        if (heureFinAutorisation != null && !heureFinAutorisation.isBefore(limiteRetard)) {
                            limiteRetard = heureFinAutorisation.plusMinutes(10);
                        }
                    }

                    if (heureArrivee != null && heureArrivee.isAfter(limiteRetard)) {
                        isEnRetard = true;
                        break;
                    }
                }
            }

            // 🔄 Si retard détecté et pas encore enregistré → on publie l'event et on enregistre le log
            if (isEnRetard) {
                RetardEvent event = new RetardEvent(collaborateur);
                eventPublisher.publishEvent(event);

                RetardLog log = RetardLog.builder()
                        .collaborateur(collaborateur)
                        .dateRetard(date)
                        .build();
                retardLogRepository.save(log);
            }
        }
    }

    public List<Map<String, Object>> calculateWorkHoursByPeriod(String matricule, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> workHoursList = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            List<Pointage> pointages = pointageRepository.findByCollaborateur_MatriculeAndDate(
                    matricule, currentDate
            );

            Conge conge = congeRepository.findByCollaborateurAndDateRange(matricule, currentDate);

            long totalMinutes = 0;
            long totalHours = 0;
            boolean hasPointage = false;
            String status = "Absent";

            for (Pointage pointage : pointages) {
                LocalTime heureArrivee = pointage.getHeure_arrivee();
                LocalTime heureDepart = pointage.getHeure_depart();

                if (heureArrivee != null && heureDepart != null) {
                    Duration duration = Duration.between(heureArrivee, heureDepart);
                    totalMinutes += duration.toMinutes();

                    LocalTime pauseDebut = LocalTime.of(12, 0);
                    LocalTime pauseFin = LocalTime.of(12, 30);

                    if (heureArrivee.isBefore(pauseDebut) && heureDepart.isAfter(pauseFin)) {
                        totalMinutes -= 60;
                    }
                    else if (heureArrivee.isBefore(pauseDebut) && !heureDepart.isBefore(pauseDebut) && heureDepart.isBefore(pauseFin)) {
                        totalMinutes -= Duration.between(pauseDebut, heureDepart).toMinutes();
                    }
                    else if (!heureArrivee.isAfter(pauseFin) && heureArrivee.isAfter(pauseDebut) && heureDepart.isAfter(pauseFin)) {
                        totalMinutes -= Duration.between(heureArrivee, pauseFin).toMinutes();
                    }
                    else if (!heureArrivee.isBefore(pauseDebut) && !heureDepart.isAfter(pauseFin)) {
                        totalMinutes -= duration.toMinutes();
                    }

                    hasPointage = true;
                    status = "Présent";
                } else if (heureArrivee != null) {
                    status = "En poste";
                    hasPointage = true;
                }
            }

            totalHours = totalMinutes / 60;
            long remainingMinutes = totalMinutes % 60;

            if (conge != null) {
                if ("CA".equals(conge.getType())) {
                    status = "Congé";
                    totalHours = 9;
                    remainingMinutes = 0;
                } else if ("A".equals(conge.getType())) {
                    status = "Autorisation";
                    if (hasPointage) {
                        status = "Présent + Autorisation";
                    }

                    if (conge.getHeureDeb() != null && conge.getHeureFin() != null) {
                        Duration autorisationDuration = Duration.between(conge.getHeureDeb(), conge.getHeureFin());
                        long autorisationMinutes = autorisationDuration.toMinutes();

                        totalMinutes += autorisationMinutes;
                        totalHours = totalMinutes / 60;
                        remainingMinutes = totalMinutes % 60;
                    }
                }
            }

            Map<String, Object> dayInfo = new HashMap<>();
            dayInfo.put("date", currentDate);
            dayInfo.put("hours", totalHours);
            dayInfo.put("minutes", remainingMinutes);
            dayInfo.put("status", status);

            workHoursList.add(dayInfo);

            currentDate = currentDate.plusDays(1);
        }

        return workHoursList;
    }



}
