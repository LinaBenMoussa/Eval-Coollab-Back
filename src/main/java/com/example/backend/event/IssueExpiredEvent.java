package com.example.backend.event;

import com.example.backend.entity.Issue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class IssueExpiredEvent {
    private final Issue issue;

}

