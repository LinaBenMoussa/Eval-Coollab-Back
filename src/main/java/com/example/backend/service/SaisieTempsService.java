package com.example.backend.service;

import com.example.backend.dto.NotificationRequestDto;
import com.example.backend.dto.ResponseSaisieTempsDto;
import com.example.backend.entity.SaisieTemps;
import com.example.backend.entity.User;
import com.example.backend.repository.SaisieTempsRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.specification.SaisieTempsSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class SaisieTempsService {

    private final SaisieTempsRepository saisieTempsRepository;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    private final EmailService emailService;

    public List<SaisieTemps> getSaisieByIssueId(Long issueId) {
        return saisieTempsRepository.findByIssueId(issueId);
    }

    public List<SaisieTemps> getSaisieByManagerId(Long managerId) {
        return saisieTempsRepository.findByCollaborateur_ManagerId(managerId);
    }

    public void checkSaisieTemps(LocalDate date) {
        List<User> collaborateurs = userRepository.findByRole("Collaborateur");

        for (User collaborateur : collaborateurs) {
            List<SaisieTemps> saisies = saisieTempsRepository.findByCollaborateurAndDate(collaborateur, date);

            if (saisies.isEmpty()) {
                String message = String.format("Vous n'avez pas effectué de saisie de temps pour le %s.", date);

                if (collaborateur.getId_bitrix24() != null) {
                    // bitrixNotificationService.sendNotification(collaborateur.getId_bitrix24(), message);
                }

                String email = collaborateur.getEmail();
                if (email != null) {
                    String subject = "Rappel : Saisie de temps manquante";
                    emailService.sendEmail("lina.b.moussa@gmail.com", subject, message);
                }

                notificationService.createNotification(new NotificationRequestDto("Rappel de saisie de temps", message, collaborateur.getId(), LocalDateTime.now()));
            }
        }
    }

    public ResponseSaisieTempsDto getSaisiesByManagerId(Long managerId, LocalDate startDate, LocalDate endDate, Long collaborateurId, int offset, int limit) {
        Specification<SaisieTemps> spec = Specification.where(null);

        if (managerId != null) {
            spec = spec.and(SaisieTempsSpecifications.hasManagerId(managerId));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and(SaisieTempsSpecifications.isBetweenDates(startDate, endDate));
        }

        if (collaborateurId != null) {
            spec = spec.and(SaisieTempsSpecifications.hasCollaborateurId(collaborateurId));
        }

        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<SaisieTemps> result = saisieTempsRepository.findAll(spec, pageable);

        return new ResponseSaisieTempsDto(result.getContent(), result.getTotalElements());
    }
}