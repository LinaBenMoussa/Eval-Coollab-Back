package com.example.backend.dto.response;
import lombok.Data;

@Data
public class EvaluationReportResponse {
    private String employeeName;
    private String period;
    private String summary;
    private String strengths;
    private String improvements;
    private String recommendations;
}