package com.example.backend.job;

import com.example.backend.service.PointageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PointageScheduler {

    private final PointageService pointageService;

    @Autowired
    public PointageScheduler(PointageService pointageService) {
        this.pointageService = pointageService;
    }

    @Scheduled(cron = "0 00 20 * * ?")
    public void checkWorkHoursDaily() {
        LocalDate today = LocalDate.now();
        pointageService.checkWorkHoursForAllCollaborateurs(today);
    }
}
