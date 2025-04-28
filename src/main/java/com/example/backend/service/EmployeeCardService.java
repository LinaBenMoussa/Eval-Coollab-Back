package com.example.backend.service;

import com.example.backend.dto.response.EmployeeCardDTO;
import com.example.backend.entity.Conge;
import com.example.backend.entity.Parametre;
import com.example.backend.entity.Pointage;
import com.example.backend.entity.User;
import com.example.backend.repository.CongeRepository;
import com.example.backend.repository.ParametreRepository;
import com.example.backend.repository.PointageRepository;
import com.example.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class EmployeeCardService {

    private final PointageRepository pointageRepository;
    private final CongeRepository congeRepository;
    private final UserRepository userRepository;
    private final ParametreRepository parametreRepository;
    private final JoursFeriesService joursFeriesService;

    public List<EmployeeCardDTO> getEmployeeCardsForManager(Long managerId, LocalDate selectedDate) {
        List<User> collaborateurs = userRepository.findByManagerId(managerId);
        List<Pointage> pointages = pointageRepository.findByDateAndCollaborateur_ManagerId(selectedDate, managerId);

        Set<String> collaborateursAvecPointage = pointages.stream()
                .map(pointage -> pointage.getCollaborateur().getMatricule())
                .collect(Collectors.toSet());

        List<EmployeeCardDTO> employeeCards = pointages.stream()
                .map(pointage -> createEmployeeCardDTO(pointage, selectedDate))
                .collect(Collectors.toList());

        for (User collaborateur : collaborateurs) {
            if (!collaborateursAvecPointage.contains(collaborateur.getMatricule())) {
                EmployeeCardDTO dto = createEmployeeCardDTOForNonPointage(collaborateur, selectedDate);
                employeeCards.add(dto);
            }
        }

        return employeeCards;
    }

    private EmployeeCardDTO createEmployeeCardDTO(Pointage pointage, LocalDate selectedDate) {
        EmployeeCardDTO dto = new EmployeeCardDTO();
        dto.setId(pointage.getId());
        dto.setDate(pointage.getDate());
        dto.setHeureArrivee(pointage.getHeure_arrivee());
        dto.setHeureDepart(pointage.getHeure_depart());
        dto.setCollaborateurNom(pointage.getCollaborateur().getNom() + " " + pointage.getCollaborateur().getPrenom());

        // Vérifier si c'est un jour férié
        if (joursFeriesService.estJourFerie(selectedDate)) {
            dto.setStatus("Jour Férié");
            dto.setWorkedHours(getRequiredHours());
            dto.setLate(false);
            dto.setCompletedWorkDay(true);
            dto.setRequiredHours(0.0); // Pas d'heures requises pour un jour férié
            return dto;
        }

        double workedHours = 0.0;
        if (pointage.getHeure_arrivee() != null && pointage.getHeure_depart() != null) {
            workedHours = calculateEffectiveWorkingHours(
                    pointage.getHeure_arrivee(),
                    pointage.getHeure_depart()
            );
        }
        dto.setWorkedHours(workedHours);

        LocalTime now = LocalTime.now();
        LocalTime cutoffTimeForArrival = getParametreAsLocalTime("heure_cutoff_arrivee", "17:00");
        LocalTime cutoffTimeForDeparture = getParametreAsLocalTime("heure_cutoff_depart", "22:00");

        if (pointage.getHeure_arrivee() != null && pointage.getHeure_depart() == null) {
            if (now.isBefore(cutoffTimeForDeparture)) {
                dto.setStatus("En Poste");
            } else {
                dto.setStatus("A quitté");
            }
        } else if (pointage.getHeure_arrivee() == null) {
            if (now.isBefore(cutoffTimeForArrival)) {
                dto.setStatus("Pas encore arrivé");
            } else {
                dto.setStatus("Absent");
            }
        } else {
            dto.setStatus("A quitté");
        }

        Conge conge = congeRepository.findByCollaborateurAndDateRange(
                pointage.getCollaborateur().getMatricule(),
                selectedDate
        );
        if (conge != null) {
            dto.setCongeType(conge.getType().equals("CA") ? "En congé" : "Autorisation");
            if ("Autorisation".equals(dto.getCongeType())) {
                double dureeAutorisation = calculateDureeAutorisation(conge.getHeureDeb(), conge.getHeureFin());
                dto.setDureeAutorisation(dureeAutorisation);
                dto.setDeb_Autorisation(conge.getHeureDeb());
                dto.setFin_Autorisation(conge.getHeureFin());
            }
        } else {
            dto.setCongeType(null);
            dto.setDureeAutorisation(0);
        }

        dto.setLate(isLate(pointage.getHeure_arrivee()));
        dto.setCompletedWorkDay(hasCompletedWorkDay(pointage, dto.getCongeType(), dto.getDureeAutorisation()));
        dto.setRequiredHours(calculateRequiredHours(dto.getCongeType(), dto.getDureeAutorisation()));

        return dto;
    }

    private EmployeeCardDTO createEmployeeCardDTOForNonPointage(User collaborateur, LocalDate selectedDate) {
        EmployeeCardDTO dto = new EmployeeCardDTO();
        dto.setId(null); // Pas de pointage, donc pas d'ID
        dto.setDate(selectedDate);
        dto.setHeureArrivee(null);
        dto.setHeureDepart(null);
        dto.setCollaborateurNom(collaborateur.getNom() + " " + collaborateur.getPrenom());

        // Vérifier si c'est un jour férié
        if (joursFeriesService.estJourFerie(selectedDate)) {
            dto.setStatus("Jour Férié");
            dto.setWorkedHours(getRequiredHours());
            dto.setLate(false);
            dto.setCompletedWorkDay(true);
            dto.setRequiredHours(0.0); // Pas d'heures requises pour un jour férié
            dto.setCongeType(null);
            dto.setDureeAutorisation(0);
            return dto;
        }

        dto.setStatus("Absent"); // Par défaut, marqué comme absent

        Conge conge = congeRepository.findByCollaborateurAndDateRange(
                collaborateur.getMatricule(),
                selectedDate
        );
        if (conge != null) {
            dto.setCongeType(conge.getType().equals("CA") ? "En congé" : "Autorisation");
            dto.setStatus(dto.getCongeType()); // Mettre à jour le statut si en congé ou autorisation
            if ("Autorisation".equals(dto.getCongeType())) {
                // Calculer la durée de l'autorisation
                double dureeAutorisation = calculateDureeAutorisation(conge.getHeureDeb(), conge.getHeureFin());
                dto.setDureeAutorisation(dureeAutorisation); // Stocker la durée de l'autorisation
                dto.setDeb_Autorisation(conge.getHeureDeb());
                dto.setFin_Autorisation(conge.getHeureFin());
            }
        } else {
            dto.setCongeType(null);
            dto.setDureeAutorisation(0); // Pas d'autorisation, durée à 0
        }
        dto.setWorkedHours(0.0);

        dto.setLate(false);
        dto.setCompletedWorkDay("En congé".equals(dto.getCongeType())); // Complété si en congé
        dto.setRequiredHours(calculateRequiredHours(dto.getCongeType(), dto.getDureeAutorisation()));

        return dto;
    }

    private boolean isLate(LocalTime heureArrivee) {
        if (heureArrivee == null) return false;

        // Récupérer l'heure de début de travail et la marge de retard (en minutes)
        LocalTime heureDebut = getParametreAsLocalTime("heure_travail_debut", "08:00");
        int margeRetard = getParametreAsInt("marge_retard", 10);

        return heureArrivee.isAfter(heureDebut.plusMinutes(margeRetard));
    }

    private boolean hasCompletedWorkDay(Pointage pointage, String congeType, double dureeAutorisation) {
        if ("En congé".equals(congeType)) {
            return true; // Journée complétée si en congé
        }

        if (pointage.getHeure_arrivee() == null || pointage.getHeure_depart() == null) {
            return false;
        }

        // Calculer les heures travaillées en tenant compte de la pause déjeuner
        double hoursWorked = calculateEffectiveWorkingHours(
                pointage.getHeure_arrivee(),
                pointage.getHeure_depart()
        );

        double requiredHours = calculateRequiredHours(congeType, dureeAutorisation);
        return hoursWorked >= requiredHours;
    }

    private double calculateEffectiveWorkingHours(LocalTime heureArrivee, LocalTime heureDepart) {
        // Récupérer les paramètres de pause déjeuner
        LocalTime pauseDejeunerDebut = getParametreAsLocalTime("pause_debut", "12:00");
        LocalTime pauseDejeunerFin = getParametreAsLocalTime("pause_fin", "13:00");

        // Calculer la durée totale
        double totalMinutes = Duration.between(heureArrivee, heureDepart).toMinutes() / 60.0;

        // Vérifier si la plage de travail chevauche la pause déjeuner
        boolean chevauchePauseDejeuner = (heureArrivee.isBefore(pauseDejeunerFin) &&
                heureDepart.isAfter(pauseDejeunerDebut));

        // Si la plage de travail chevauche la pause déjeuner, soustraire la durée appropriée
        if (chevauchePauseDejeuner) {
            // Calculer le chevauchement réel
            LocalTime debutChevauchement = heureArrivee.isBefore(pauseDejeunerDebut) ?
                    pauseDejeunerDebut : heureArrivee;
            LocalTime finChevauchement = heureDepart.isAfter(pauseDejeunerFin) ?
                    pauseDejeunerFin : heureDepart;

            // Durée du chevauchement (en heures)
            double dureeChevauchement = Duration.between(debutChevauchement, finChevauchement).toMinutes() / 60.0;
            double dureePauseMax = getParametreAsDouble("duree_pause_dejeuner", 1.0);

            // Soustraire seulement le temps de chevauchement réel (max durée de pause)
            totalMinutes -= Math.min(dureeChevauchement, dureePauseMax);
        }

        return Math.max(totalMinutes, 0);
    }

    private double calculateRequiredHours(String congeType, double dureeAutorisation) {
        if ("En congé".equals(congeType)) {
            return 0; // Pas d'heures requises si en congé
        }

        double requiredHours = getRequiredHours();

        if ("Autorisation".equals(congeType)) {
            requiredHours -= dureeAutorisation;
        }

        return Math.max(requiredHours, 0);
    }

    private double getRequiredHours() {
        // Charger les paramètres de temps de travail
        LocalTime heureFin = getParametreAsLocalTime("heure_travail_fin", "17:30");
        LocalTime heureDebut = getParametreAsLocalTime("heure_travail_debut", "08:00");
        double dureePauseDejeuner = getParametreAsDouble("duree_pause_dejeuner", 1.0);

        // Calculer le temps de travail total
        long minutes = Duration.between(heureDebut, heureFin).toMinutes();
        double heures = minutes / 60.0;

        // Retirer la durée de la pause déjeuner
        return heures - dureePauseDejeuner;
    }

    private double calculateDureeAutorisation(LocalTime heureDeb, LocalTime heureFin) {
        if (heureDeb == null || heureFin == null) {
            return 0;
        }
        double dureeMinutes = Duration.between(heureDeb, heureFin).toMinutes() / 60.0;
        return dureeMinutes;
    }

    // Méthodes utilitaires pour récupérer les paramètres
    private LocalTime getParametreAsLocalTime(String cle, String defaultValue) {
        String value = parametreRepository.findByCle(cle)
                .map(Parametre::getValeur)
                .orElse(defaultValue);
        return LocalTime.parse(value);
    }

    private double getParametreAsDouble(String cle, double defaultValue) {
        return parametreRepository.findByCle(cle)
                .map(parametre -> Double.parseDouble(parametre.getValeur()))
                .orElse(defaultValue);
    }

    private int getParametreAsInt(String cle, int defaultValue) {
        return parametreRepository.findByCle(cle)
                .map(parametre -> Integer.parseInt(parametre.getValeur()))
                .orElse(defaultValue);
    }
}