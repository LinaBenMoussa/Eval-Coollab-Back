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

    @Scheduled(cron = "0 00 20 * * ?")
    public void checkWorkHoursDaily() {
        LocalDate today = LocalDate.now();
        pointageService.checkWorkHoursForAllCollaborateurs(today);
    }
}
