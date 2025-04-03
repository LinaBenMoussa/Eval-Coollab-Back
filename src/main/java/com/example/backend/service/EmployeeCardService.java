package com.example.backend.service;

import com.example.backend.dto.EmployeeCardDTO;
import com.example.backend.entity.Conge;
import com.example.backend.entity.Pointage;
import com.example.backend.entity.User;
import com.example.backend.repository.CongeRepository;
import com.example.backend.repository.PointageRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeCardService {

    @Autowired
    private PointageRepository pointageRepository;

    @Autowired
    private CongeRepository congeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<EmployeeCardDTO> getEmployeeCardsForManager(Long managerId, LocalDate selectedDate) {
        // Récupérer tous les collaborateurs du manager
        List<User> collaborateurs = userRepository.findByManagerId(managerId);

        // Récupérer les pointages pour la date sélectionnée
        List<Pointage> pointages = pointageRepository.findByDateAndCollaborateur_ManagerId(selectedDate, managerId);

        // Créer un ensemble des matricules des collaborateurs ayant un pointage
        Set<String> collaborateursAvecPointage = pointages.stream()
                .map(pointage -> pointage.getCollaborateur().getMatricule())
                .collect(Collectors.toSet());

        // Transformer les pointages en DTOs
        List<EmployeeCardDTO> employeeCards = pointages.stream()
                .map(pointage -> createEmployeeCardDTO(pointage, selectedDate))
                .collect(Collectors.toList());

        // Ajouter les collaborateurs sans pointage
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
        dto.setCollaborateurNom(pointage.getCollaborateur().getNom()+" "+pointage.getCollaborateur().getPrenom());

        // Déterminer le statut en fonction des pointages et de l'heure actuelle
        LocalTime now = LocalTime.now();
        LocalTime cutoffTimeForArrival = LocalTime.of(17, 0); // 17:00
        LocalTime cutoffTimeForDeparture = LocalTime.of(22, 0); // 22:00

        if (pointage.getHeure_arrivee() != null && pointage.getHeure_depart() == null) {
            // Si le collaborateur a fait un pointage de début mais pas de fin
            if (now.isBefore(cutoffTimeForDeparture)) {
                dto.setStatus("En Poste"); // En poste si avant 22:00
            } else {
                dto.setStatus("A quitté"); // A quitté si après 22:00
            }
        } else if (pointage.getHeure_arrivee() == null) {
            // Si le collaborateur n'a pas fait de pointage de début
            if (now.isBefore(cutoffTimeForArrival)) {
                dto.setStatus("Pas encore arrivé"); // Pas encore arrivé si avant 17:00
            } else {
                dto.setStatus("Absent"); // Absent si après 17:00
            }
        } else {
            // Si le collaborateur a fait un pointage de début et de fin
            dto.setStatus("A quitté"); // Par défaut, marqué comme "A quitté"
        }

        // Vérifier si le collaborateur est en congé ou en autorisation
        Conge conge = congeRepository.findByCollaborateurAndDateRange(
                pointage.getCollaborateur().getMatricule(),
                selectedDate
        );
        if (conge != null) {
            dto.setCongeType(conge.getType().equals("CA") ? "En congé" : "Autorisation");
            if ("Autorisation".equals(dto.getCongeType())) {
                // Calculer la durée de l'autorisation
                double dureeAutorisation = calculateDureeAutorisation(conge.getHeureDeb(), conge.getHeureFin());
                dto.setDureeAutorisation(dureeAutorisation);
                dto.setDeb_Autorisation(conge.getHeureDeb());
                dto.setFin_Autorisation(conge.getHeureFin());
            }
        } else {
            dto.setCongeType(null);
            dto.setDureeAutorisation(0);
        }

        // Calculer si le collaborateur est en retard
        dto.setLate(isLate(pointage.getHeure_arrivee()));

        // Calculer si le collaborateur a fait ses heures
        dto.setCompletedWorkDay(hasCompletedWorkDay(pointage, dto.getCongeType(), dto.getDureeAutorisation()));

        // Calculer les heures requises
        dto.setRequiredHours(calculateRequiredHours(dto.getCongeType(), dto.getDureeAutorisation()));

        return dto;
    }

    private EmployeeCardDTO createEmployeeCardDTOForNonPointage(User collaborateur, LocalDate selectedDate) {
        EmployeeCardDTO dto = new EmployeeCardDTO();
        dto.setId(null); // Pas de pointage, donc pas d'ID
        dto.setDate(selectedDate);
        dto.setHeureArrivee(null);
        dto.setHeureDepart(null);
        dto.setStatus("Absent"); // Par défaut, marqué comme absent
        dto.setCollaborateurNom(collaborateur.getNom()+" "+collaborateur.getPrenom());

        // Vérifier si le collaborateur est en congé ou en autorisation
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
            }
        } else {
            dto.setCongeType(null);
            dto.setDureeAutorisation(0); // Pas d'autorisation, durée à 0
        }

        // Pas de pointage, donc pas de retard ni d'heures travaillées
        dto.setLate(false);
        dto.setCompletedWorkDay(false);
        dto.setRequiredHours(calculateRequiredHours(dto.getCongeType(), dto.getDureeAutorisation()));

        return dto;
    }

    private boolean isLate(LocalTime heureArrivee) {
        if (heureArrivee == null) return false;
        return heureArrivee.isAfter(LocalTime.of(8, 10)); // Retard après 8h10
    }

    private boolean hasCompletedWorkDay(Pointage pointage, String congeType, double dureeAutorisation) {
        if (pointage.getHeure_arrivee() == null || pointage.getHeure_depart() == null) {
            return false;
        }
        long hoursWorked = Duration.between(pointage.getHeure_arrivee(), pointage.getHeure_depart()).toHours();
        double requiredHours = calculateRequiredHours(congeType, dureeAutorisation);
        return hoursWorked >= requiredHours-0.10;
    }

    private double calculateRequiredHours(String congeType, double dureeAutorisation) {
        double requiredHours = 9; // Heures requises par défaut

        if ("Autorisation".equals(congeType)) {
            requiredHours -= dureeAutorisation; // Soustraction de la durée de l'autorisation
        }

        return Math.max(requiredHours, 0); // Assurez-vous que les heures requises ne soient pas négatives
    }

    private double calculateDureeAutorisation(LocalTime heureDeb, LocalTime heureFin) {
        if (heureDeb == null || heureFin == null) {
            return 0; // Pas d'autorisation si les heures ne sont pas définies
        }
        return Duration.between(heureDeb, heureFin).toHours();
    }
}