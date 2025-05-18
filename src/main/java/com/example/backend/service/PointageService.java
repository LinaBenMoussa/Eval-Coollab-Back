package com.example.backend.service;

import com.example.backend.dto.request.NotificationRequestDto;
import com.example.backend.dto.request.PointageRequestDto;
import com.example.backend.dto.response.PointageResponseDto;
import com.example.backend.entity.*;
import com.example.backend.event.RetardEvent;
import com.example.backend.repository.*;
import com.example.backend.specification.PointageSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class PointageService {

    private final PointageRepository pointageRepository;
    private final ParametreRepository parametreRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final BitrixNotificationService bitrixNotificationService;
    private final EmailService emailService;
    private final CongeRepository congeRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RetardLogRepository retardLogRepository;
    private final JoursFeriesService joursFeriesService;

    public double getRequiredHours() {
        // 1. Charger les paramètres
        String heureFinStr = parametreRepository.findByCle("heure_travail_fin")
                .map(Parametre::getValeur)
                .orElse("17:30");

        String heureDebutStr = parametreRepository.findByCle("heure_travail_debut")
                .map(Parametre::getValeur)
                .orElse("08:00");

        LocalTime heureFin = LocalTime.parse(heureFinStr);
        LocalTime heureDebut = LocalTime.parse(heureDebutStr);

        long minutes = Duration.between(heureDebut, heureFin).toMinutes();
        double heures = minutes / 60.0;

        // 4. Retirer 1 heure pour la pause déjeuner
        return heures - Double.parseDouble(
                parametreRepository.findByCle("duree_pause_dejeuner")
                        .map(Parametre::getValeur)
                        .orElse("1")
        );
    }

    public void checkWorkHoursForAllCollaborateurs(LocalDate date) {
        // Si c'est un jour férié, ne pas vérifier les heures de travail
        if (joursFeriesService.estJourFerie(date)) {
            return;
        }

        List<User> collaborateurs = userRepository.findByRole("Collaborateur");

        for (User collaborateur : collaborateurs) {
            List<Pointage> pointages = pointageRepository.findByCollaborateur_MatriculeAndDate(
                    collaborateur.getMatricule(), date
            );
            LocalDate selectedDate = date;
            String matricule = collaborateur.getMatricule();
            double REQUIRED_HOURS = getRequiredHours();

            Conge conge = congeRepository.findByCollaborateurAndDateRange(matricule, selectedDate);
            double congeHours = 0;
            double autorisationHours = 0;

            if (conge != null) {
                if ("CA".equals(conge.getType())) {
                    congeHours = getRequiredHours();
                } else if ("A".equals(conge.getType())) {
                    autorisationHours = Duration.between(conge.getHeureDeb(), conge.getHeureFin()).toHours();
                }
            }
            double requiredHoursAdjusted = Math.max(0, REQUIRED_HOURS - (congeHours + autorisationHours));

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

        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "date"));


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
        // Si c'est un jour férié, ne pas vérifier les retards
        if (joursFeriesService.estJourFerie(date)) {
            return;
        }

        List<User> collaborateurs = userRepository.findByRole("Collaborateur");

        for (User collaborateur : collaborateurs) {
            String matricule = collaborateur.getMatricule();

            boolean dejaSignale = retardLogRepository
                    .findByCollaborateur_MatriculeAndDateRetard(matricule, date)
                    .isPresent();

            if (dejaSignale) continue;

            List<Pointage> pointages = pointageRepository.findByCollaborateur_MatriculeAndDate(matricule, date);
            Conge conge = congeRepository.findByCollaborateurAndDateRange(matricule, date);
            LocalTime now = LocalTime.now();
            String heureDebutStr = parametreRepository.findByCle("heure_travail_debut")
                    .map(Parametre::getValeur)
                    .orElse("08:00");

            LocalTime heureDebut = LocalTime.parse(heureDebutStr);
            LocalTime limiteRetard = heureDebut.plusMinutes(10);

            if (conge != null && "CA".equals(conge.getType())) {
                continue;
            }

            boolean isEnRetard = false;

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
            // Initialize default values
            long totalMinutes = 0;
            double workHours = 0;
            boolean hasPointage = false;
            String status = "Absent";
            LocalTime heureArrivee = null;
            LocalTime heureDepart = null;

            // Variables pour stocker les informations d'autorisation et de congé
            LocalTime autorisationDebut = null;
            LocalTime autorisationFin = null;
            double dureeConge = 0;

            // Vérifier si c'est un jour férié ou un weekend
            if (joursFeriesService.estJourFerie(currentDate)) {
                status = "Jour Férié";
                workHours = getRequiredHours();

                Map<String, Object> dayInfo = new HashMap<>();
                dayInfo.put("date", currentDate);
                dayInfo.put("heureArrivee", heureArrivee);
                dayInfo.put("heureDepart", heureDepart);
                dayInfo.put("workHours", 0);
                dayInfo.put("status", status);
                dayInfo.put("autorisationDebut", autorisationDebut);
                dayInfo.put("autorisationFin", autorisationFin);
                dayInfo.put("dureeConge", dureeConge);

                workHoursList.add(dayInfo);
                currentDate = currentDate.plusDays(1);
                continue;
            }

            // Vérifier si c'est un weekend
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                status = "Weekend";

                Map<String, Object> dayInfo = new HashMap<>();
                dayInfo.put("date", currentDate);
                dayInfo.put("heureArrivee", heureArrivee);
                dayInfo.put("heureDepart", heureDepart);
                dayInfo.put("workHours", 0);
                dayInfo.put("status", status);
                dayInfo.put("autorisationDebut", autorisationDebut);
                dayInfo.put("autorisationFin", autorisationFin);
                dayInfo.put("dureeConge", dureeConge);

                workHoursList.add(dayInfo);
                currentDate = currentDate.plusDays(1);
                continue;
            }

            // Récupérer les pointages pour ce jour
            List<Pointage> pointages = pointageRepository.findByCollaborateur_MatriculeAndDate(
                    matricule, currentDate
            );

            // Récupérer le congé pour ce jour
            Conge conge = congeRepository.findByCollaborateurAndDateRange(matricule, currentDate);

            // Récupérer les heures de pause
            String pauseDebutStr = parametreRepository.findByCle("pause_debut")
                    .map(Parametre::getValeur)
                    .orElse("12:30"); // par défaut midi

            String pauseFinStr = parametreRepository.findByCle("pause_fin")
                    .map(Parametre::getValeur)
                    .orElse("13:30"); // par défaut 13h

            try {
                LocalTime pauseDebut = LocalTime.parse(pauseDebutStr);
                LocalTime pauseFin = LocalTime.parse(pauseFinStr);

                // Traiter les pointages
                for (Pointage pointage : pointages) {
                    LocalTime pointageArrivee = pointage.getHeure_arrivee();
                    LocalTime pointageDepart = pointage.getHeure_depart();

                    // Garder trace du premier pointage d'arrivée et du dernier de départ pour l'affichage
                    if (pointageArrivee != null) {
                        if (heureArrivee == null || pointageArrivee.isBefore(heureArrivee)) {
                            heureArrivee = pointageArrivee;
                        }
                    }

                    if (pointageDepart != null) {
                        if (heureDepart == null || pointageDepart.isAfter(heureDepart)) {
                            heureDepart = pointageDepart;
                        }
                    }

                    if (pointageArrivee != null && pointageDepart != null) {
                        Duration duration = Duration.between(pointageArrivee, pointageDepart);
                        long pointageMinutes = duration.toMinutes();

                        // Soustraire la pause si nécessaire
                        if (pointageArrivee.isBefore(pauseDebut) && pointageDepart.isAfter(pauseFin)) {
                            // La journée entière couvre la pause
                            pointageMinutes -= Duration.between(pauseDebut, pauseFin).toMinutes();
                        } else if (pointageArrivee.isBefore(pauseDebut) && pointageDepart.isAfter(pauseDebut) && pointageDepart.isBefore(pauseFin)) {
                            // Départ pendant la pause
                            pointageMinutes -= Duration.between(pauseDebut, pointageDepart).toMinutes();
                        } else if (pointageArrivee.isAfter(pauseDebut) && pointageArrivee.isBefore(pauseFin) && pointageDepart.isAfter(pauseFin)) {
                            // Arrivée pendant la pause
                            pointageMinutes -= Duration.between(pointageArrivee, pauseFin).toMinutes();
                        } else if (pointageArrivee.isAfter(pauseDebut) && pointageDepart.isBefore(pauseFin)) {
                            // Entièrement pendant la pause
                            pointageMinutes = 0; // Pas de temps de travail valide
                        }

                        totalMinutes += pointageMinutes;
                        hasPointage = true;
                        status = "Présent";
                    } else if (pointageArrivee != null) {
                        status = "En poste";
                        hasPointage = true;
                    }
                }

                // Traitement des congés
                if (conge != null) {
                    if ("CA".equals(conge.getType())) {
                        if (hasPointage) {
                            status = "Présent + Congé";
                            // Pour un congé partiel, calculer la durée proportionnelle
                            dureeConge = (conge.getNbrjour() < 1.0) ?
                                    conge.getNbrjour() * getRequiredHours() :
                                    getRequiredHours();
                        } else {
                            status = "Congé";
                            dureeConge = getRequiredHours(); // Un jour complet
                            // Pour un congé complet, ajuster les heures de travail
                            totalMinutes += dureeConge * 60; // Convertir heures en minutes
                        }
                    } else if ("A".equals(conge.getType())) {
                        if (hasPointage) {
                            status = "Présent + Autorisation";
                        } else {
                            status = "Autorisation";
                        }

                        // Récupérer les heures de début et fin d'autorisation
                        autorisationDebut = conge.getHeureDeb();
                        autorisationFin = conge.getHeureFin();

                    }
                }

                // Convertir les minutes totales en heures avec précision à 2 décimales
                workHours = Math.round((totalMinutes / 60.0) * 100.0) / 100.0;

            } catch (DateTimeParseException e) {
                // Gestion des erreurs de parsing
                status += " (Erreur)";
            }

            Map<String, Object> dayInfo = new HashMap<>();
            dayInfo.put("date", currentDate);
            dayInfo.put("heureArrivee", heureArrivee);
            dayInfo.put("heureDepart", heureDepart);
            dayInfo.put("workHours", workHours);
            dayInfo.put("status", status);
            dayInfo.put("autorisationDebut", autorisationDebut);
            dayInfo.put("autorisationFin", autorisationFin);
            dayInfo.put("dureeConge", dureeConge);

            workHoursList.add(dayInfo);
            currentDate = currentDate.plusDays(1);
        }

        return workHoursList;
    }
}