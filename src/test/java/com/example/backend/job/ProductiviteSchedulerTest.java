package com.example.backend.job;

import com.example.backend.service.CollaborateurStatsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Date;

@SpringBootTest
public class ProductiviteSchedulerTest {

    @Mock
    private CollaborateurStatsService collaborateurStatsService;

    @InjectMocks
    private ProductiviteScheduler productiviteScheduler;

    @Test
    public void testExecuteMonthlyProductivityJob() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.APRIL, 1);
        Date currentDate = calendar.getTime();

        calendar.add(Calendar.MONTH, -1); // Recule d'un mois
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Premier jour du mois précédent
        java.sql.Date expectedStartDate = new java.sql.Date(calendar.getTimeInMillis());

        Mockito.doNothing().when(collaborateurStatsService).generateMonthlyProductivityForAllCollaborateurs(expectedStartDate);

        productiviteScheduler.executeMonthlyProductivityJob();

        Mockito.verify(collaborateurStatsService, Mockito.times(1)).generateMonthlyProductivityForAllCollaborateurs(expectedStartDate);
    }

}
