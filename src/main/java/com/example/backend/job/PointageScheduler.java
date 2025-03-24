package com.example.backend.job;

import com.example.backend.service.PointageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PointageScheduler {

    @Autowired
    private PointageService pointageService;

    @Scheduled(cron = "0 31 0 * * ?")
    public void checkWorkHoursDaily() {
        System.out.println("notif se declenche");
        LocalDate today = LocalDate.now();
        pointageService.checkWorkHoursForAllCollaborateurs(today);
    }
}
