package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {
    private List<ProjectWithIssueCountDto> projects;
    private long total;
}