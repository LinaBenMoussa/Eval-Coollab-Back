package com.example.backend.dto.response;

import com.example.backend.entity.Issue;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IssueWithTimeEntryCountDto {
    private Issue issue;
    private long timeEntryCount;
}
