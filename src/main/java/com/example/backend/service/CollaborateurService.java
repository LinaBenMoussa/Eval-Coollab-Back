package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CollaborateurService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getCollaborateurStats(Long collaborateurId, Date startDate, Date endDate) {
        Map<String, Object> stats = new HashMap<>();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CalculateCollaborateurStats")
                .declareParameters(
                        new SqlParameter("collaborateur_id", Types.INTEGER),
                        new SqlParameter("start_date", Types.DATE),         // Nouveau paramètre d'entrée
                        new SqlParameter("end_date", Types.DATE),           // Nouveau paramètre d'entrée
                        new SqlOutParameter("positive_rate", Types.FLOAT),
                        new SqlOutParameter("negative_rate", Types.FLOAT),
                        new SqlOutParameter("retard_rate", Types.FLOAT),
                        new SqlOutParameter("respect_echeance_rate", Types.FLOAT),
                        new SqlOutParameter("total_hours_missing", Types.FLOAT),
                        new SqlOutParameter("tasks_in_late", Types.INTEGER),
                        new SqlOutParameter("productivity_score", Types.FLOAT),
                        new SqlOutParameter("average_task_completion_time", Types.FLOAT),
                        new SqlOutParameter("conge_utilization_rate", Types.FLOAT),
                        new SqlOutParameter("overtime_hours", Types.FLOAT),
                        new SqlOutParameter("task_complexity_score", Types.FLOAT),
                        new SqlOutParameter("task_completion_rate", Types.FLOAT),
                        new SqlOutParameter("daily_avg_working_hours", Types.FLOAT),
                        new SqlOutParameter("temps_moyen_retard", Types.FLOAT)
                );

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("collaborateur_id", collaborateurId)
                .addValue("start_date", startDate)  // Passage de la date de début
                .addValue("end_date", endDate);     // Passage de la date de fin

        Map<String, Object> result = jdbcCall.execute(in);

        // Transférer les résultats dans notre map
        stats.put("positiveRate", result.get("positive_rate"));
        stats.put("negativeRate", result.get("negative_rate"));
        stats.put("retardRate", result.get("retard_rate"));
        stats.put("respectEcheanceRate", result.get("respect_echeance_rate"));
        stats.put("totalHoursMissing", result.get("total_hours_missing"));
        stats.put("tasksInLate", result.get("tasks_in_late"));
        stats.put("productivityScore", result.get("productivity_score"));
        stats.put("averageTaskCompletionTime", result.get("average_task_completion_time"));
        stats.put("congeUtilizationRate", result.get("conge_utilization_rate"));
        stats.put("overtimeHours", result.get("overtime_hours"));
        stats.put("taskComplexityScore", result.get("task_complexity_score"));
        stats.put("taskCompletionRate", result.get("task_completion_rate"));
        stats.put("dailyAvgWorkingHours", result.get("daily_avg_working_hours"));
        stats.put("tempsMoyenRetard", result.get("temps_moyen_retard"));

        return stats;
    }

}