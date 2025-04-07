package com.example.backend.job;

import com.example.backend.service.BadgeService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

@AllArgsConstructor
@Component
public class BadgeScheduler {

    private final BadgeService badgeService;

    @Scheduled(cron = "0 13 10 * * ?")
    public void executeMonthlyProductivityJob() {
        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);

        badgeService.attribuerBadges(startDate);

    }
}
