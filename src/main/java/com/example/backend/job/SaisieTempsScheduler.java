package com.example.backend.job;

import com.example.backend.service.SaisieTempsService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@AllArgsConstructor
@Component
public class SaisieTempsScheduler {

    private final SaisieTempsService saisieTempsService;

    @Scheduled(cron = "0 30 09 * * ?")
    public void checkSaisieTemps() {
        LocalDate today = LocalDate.now();
        saisieTempsService.checkSaisieTemps(today);
    }

}
