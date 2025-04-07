package com.example.backend.job;

import com.example.backend.repository.ProductiviteRepository;
import com.example.backend.service.CollaborateurStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class ProductiviteScheduler {

    private final ProductiviteRepository productiviteRepository;

    private final CollaborateurStatsService collaborateurStatsService;

    // Format de la date pour obtenir le mois précédent
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public ProductiviteScheduler(ProductiviteRepository productiviteRepository, CollaborateurStatsService collaborateurStatsService) {
        this.productiviteRepository = productiviteRepository;
        this.collaborateurStatsService = collaborateurStatsService;
    }

    @Scheduled(cron = "0 59 12 * * ?")
    public void executeMonthlyProductivityJob() {
        productiviteRepository.deleteAll();
        Date currentDate = new Date();

        // Créer un objet Calendar pour manipuler les dates
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        // Reculer d'un mois
        calendar.add(Calendar.MONTH, -1);

        // Mettre à zéro le jour du mois pour obtenir le premier jour du mois précédent
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Formater la date pour obtenir la chaîne au format YYYY-MM-DD
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr = dateFormat.format(calendar.getTime());
        Date startDate = java.sql.Date.valueOf(startDateStr);

        collaborateurStatsService.generateMonthlyProductivityForAllCollaborateurs(startDate);

        System.out.println("Le job mensuel a été exécuté pour la période: " + startDateStr);
    }
}
