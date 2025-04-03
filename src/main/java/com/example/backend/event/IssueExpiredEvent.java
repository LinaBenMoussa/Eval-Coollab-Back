package com.example.backend.event;

import com.example.backend.entity.Issue;

public class IssueExpiredEvent {
    private final Issue issue;

    public IssueExpiredEvent(Issue issue) {
        this.issue = issue;
    }

    public Issue getIssue() {
        return issue;
    }
}

