package com.example.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class CollaborateurStatsService {

    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> getCollaborateurStats(Long collaborateurId, Date startDate, Date endDate, String periodType) {
        Map<String, Object> stats = new HashMap<>();

        SimpleJdbcCall statsCall = new SimpleJdbcCall(jdbcTemplate)
                .withoutProcedureColumnMetaDataAccess()
                .withProcedureName("CalculateCollaborateurStats")
                .declareParameters(
                        new SqlParameter("p_collaborateur_id", Types.INTEGER),
                        new SqlParameter("start_date", Types.DATE),
                        new SqlParameter("end_date", Types.DATE),
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
                        new SqlOutParameter("task_total_echeance_not_null", Types.FLOAT),
                        new SqlOutParameter("task_completion_rate", Types.FLOAT),
                        new SqlOutParameter("daily_avg_working_hours", Types.FLOAT),
                        new SqlOutParameter("temps_moyen_retard", Types.FLOAT)
                );

        SqlParameterSource statsParams = new MapSqlParameterSource()
                .addValue("p_collaborateur_id", collaborateurId)
                .addValue("start_date", startDate)
                .addValue("end_date", endDate);

        Map<String, Object> statsResult = statsCall.execute(statsParams);

        stats.put("positiveRate", statsResult.get("positive_rate"));
        stats.put("negativeRate", statsResult.get("negative_rate"));
        stats.put("retardRate", statsResult.get("retard_rate"));
        stats.put("respectEcheanceRate", statsResult.get("respect_echeance_rate"));
        stats.put("totalHoursMissing", statsResult.get("total_hours_missing"));
        stats.put("tasksInLate", statsResult.get("tasks_in_late"));
        stats.put("productivityScore", statsResult.get("productivity_score"));
        stats.put("averageTaskCompletionTime", statsResult.get("average_task_completion_time"));
        stats.put("congeUtilizationRate", statsResult.get("conge_utilization_rate"));
        stats.put("overtimeHours", statsResult.get("overtime_hours"));
        stats.put("task_total_echeance_not_null", statsResult.get("task_total_echeance_not_null"));
        stats.put("taskCompletionRate", statsResult.get("task_completion_rate"));
        stats.put("dailyAvgWorkingHours", statsResult.get("daily_avg_working_hours"));
        stats.put("tempsMoyenRetard", statsResult.get("temps_moyen_retard"));

        SimpleJdbcCall curveCall = new SimpleJdbcCall(jdbcTemplate)
                .withoutProcedureColumnMetaDataAccess()
                .withProcedureName("CalculateProductivityCurve")
                .declareParameters(
                        new SqlParameter("p_collaborateur_id", Types.INTEGER),
                        new SqlParameter("start_date", Types.DATE),
                        new SqlParameter("end_date", Types.DATE),
                        new SqlParameter("period_type", Types.VARCHAR),
                        new SqlOutParameter("productivity_scores", Types.VARCHAR), // JSON
                        new SqlOutParameter("period_labels", Types.VARCHAR)        // JSON
                );

        SqlParameterSource curveParams = new MapSqlParameterSource()
                .addValue("p_collaborateur_id", collaborateurId)
                .addValue("start_date", startDate)
                .addValue("end_date", endDate)
                .addValue("period_type", periodType);

        Map<String, Object> curveResult = curveCall.execute(curveParams);

        stats.put("productivityScores", curveResult.get("productivity_scores"));
        stats.put("periodLabels", curveResult.get("period_labels"));

        return stats;
    }

    public void generateMonthlyProductivityForAllCollaborateurs(Date startDate) {
        SimpleJdbcCall monthlyProductivityCall = new SimpleJdbcCall(jdbcTemplate)
                .withoutProcedureColumnMetaDataAccess()
                .withProcedureName("GenerateMonthlyProductivityForAllCollaborateurs")
                .declareParameters(
                        new SqlParameter("start_date", Types.DATE)
                );

        Map<String, Object> params = Map.of(
                "start_date", startDate
        );

        monthlyProductivityCall.execute(params);
    }
}