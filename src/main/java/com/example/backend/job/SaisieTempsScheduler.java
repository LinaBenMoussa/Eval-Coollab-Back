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

    @Scheduled(cron = "0 30 10 * * ?")
    public void checkSaisieTemps() {
        LocalDate day = LocalDate.now().minusDays(1);
        saisieTempsService.checkSaisieTemps(day);
    }

}
