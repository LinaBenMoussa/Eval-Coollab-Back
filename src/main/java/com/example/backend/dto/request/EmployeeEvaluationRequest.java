package com.example.backend.dto.request;

import lombok.Data;

@Data
public class EmployeeEvaluationRequest {
    private String employeeId;
    private String employeeName;
    private String period;
}