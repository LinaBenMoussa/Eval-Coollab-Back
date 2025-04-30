package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectWithIssueCountDto {
    private Long id;
    private String name;
    private String identifier;
    private int status;
    private long issueCount;
}
