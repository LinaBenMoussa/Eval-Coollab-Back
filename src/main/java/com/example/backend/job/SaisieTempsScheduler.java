package com.example.backend.job;

import com.example.backend.service.SaisieTempsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SaisieTempsScheduler {

    private final SaisieTempsService saisieTempsService;

    @Autowired
    public SaisieTempsScheduler(SaisieTempsService saisieTempsService) {
        this.saisieTempsService = saisieTempsService;
    }

    @Scheduled(cron = "0 09 17 * * ?")
    public void checkSaisieTemps() {
        LocalDate today = LocalDate.now();
        saisieTempsService.checkSaisieTemps(today);
    }

}
