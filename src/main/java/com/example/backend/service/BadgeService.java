package com.example.backend.service;

import com.example.backend.entity.Badge;
import com.example.backend.entity.EmployeBadge;
import com.example.backend.entity.User;
import com.example.backend.repository.BadgeRepository;
import com.example.backend.repository.EmployeBadgeRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final EmployeBadgeRepository employeBadgeRepository;
    private final CollaborateurStatsService collaborateurStatsService;

    public void attribuerBadges(LocalDate mois) {
        List<User> users = userRepository.findAll();

        Date startDate = Date.from(mois.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(mois.withDayOfMonth(mois.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());

        for (User user : users) {
            Map<String, Object> stats = collaborateurStatsService.getCollaborateurStats(
                    user.getId(), startDate, endDate, "month"
            );

            Float retardRate = stats.get("retardRate") instanceof Double ?
                    ((Double) stats.get("retardRate")).floatValue() : (Float) stats.get("retardRate");

            Float taskCompletionRate = stats.get("taskCompletionRate") instanceof Double ?
                    ((Double) stats.get("taskCompletionRate")).floatValue() : (Float) stats.get("taskCompletionRate");

            Float respectEcheanceRate = stats.get("respectEcheanceRate") instanceof Double ?
                    ((Double) stats.get("respectEcheanceRate")).floatValue() : (Float) stats.get("respectEcheanceRate");

            Float productivityScore = stats.get("productivityScore") instanceof Double ?
                    ((Double) stats.get("productivityScore")).floatValue() : (Float) stats.get("productivityScore");

            Float totalHoursMissing = stats.get("totalHoursMissing") instanceof Double ?
                    ((Double) stats.get("totalHoursMissing")).floatValue() : (Float) stats.get("totalHoursMissing");

            Integer tasksInLate = (Integer) stats.get("tasksInLate");

            // Conditions sur les taux et scores
            if (retardRate != null && retardRate == 0f) {
                attribuerBadge(user, "Toujours à l'heure", mois);
            }

            if (totalHoursMissing != null && totalHoursMissing <= 1) {
                attribuerBadge(user, "Régulier", mois);
            }

            if (taskCompletionRate != null && taskCompletionRate == 100f) {
                attribuerBadge(user, "Assidu", mois);
            }

            if (respectEcheanceRate != null && respectEcheanceRate == 100f && tasksInLate != null && tasksInLate == 0) {
                attribuerBadge(user, "Objectif atteint", mois);
            }

            if (productivityScore != null && productivityScore >= 85f) {
                attribuerBadge(user, "Employé du mois", mois);
            }
        }
    }


    private void attribuerBadge(User user, String nomBadge, LocalDate mois) {
        Badge badge = badgeRepository.findByName(nomBadge);
        if (badge == null) return;
        EmployeBadge employeBadge= employeBadgeRepository.findByCollaborateur_IdAndBadge_Id(user.getId(), badge.getId());
        if(employeBadge!=null){
            employeBadge.setNbrBadge(employeBadge.getNbrBadge()+1);
        }
        else{
            employeBadge = new EmployeBadge();
            employeBadge.setCollaborateur(user);
            employeBadge.setBadge(badge);
            employeBadge.setDateAttribution(mois.withDayOfMonth(1));
            employeBadge.setNbrBadge(1);
        }
        employeBadgeRepository.save(employeBadge);

    }


    public List<EmployeBadge> getBadgesByCollaborateur(Long collaborateurId){
        return employeBadgeRepository.findByCollaborateur_Id(collaborateurId);
    }
}