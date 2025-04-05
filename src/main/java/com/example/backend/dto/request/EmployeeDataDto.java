package com.example.backend.dto.request;
import lombok.Data;

@Data
public class EmployeeDataDto {
    private String employeeName;
    private String period;
    private double positiveRate;
    private double respectEcheanceRate;
    private double taskComplexityScore;
    private double averageTaskCompletionTime;
    private double congeUtilizationRate;
    private double dailyAvgWorkingHours;
    private double negativeRate;
    private double overtimeHours;
    private String periodLabels;
    private double productivityScore;
    private String productivityScores;
    private double retardRate;
    private double taskCompletionRate;
    private int tasksInLate;
    private double tempsMoyenRetard;
    private double totalHoursMissing;
}