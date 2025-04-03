package com.example.backend.dto.response;

import com.example.backend.entity.Issue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IssueResponseDto {
    private List<Issue> issues;
    private long total;
}
