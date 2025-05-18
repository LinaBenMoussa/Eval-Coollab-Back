package com.example.backend.job;

import com.example.backend.service.PointageService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
@AllArgsConstructor
@Component
public class PointageScheduler {

    private final PointageService pointageService;

    @Scheduled(cron = "0 30 08 * * ?")
    public void checkWorkHoursDaily() {
        LocalDate day = LocalDate.now().minusDays(1);;
        pointageService.checkWorkHoursForAllCollaborateurs(day);
    }
    @Scheduled(cron = "0 30 10 * * ?")
    public void checkIsLate() {
        LocalDate day = LocalDate.now();
        pointageService.checkIsLate(day);
    }
}
