package com.example.backend.listener;

import com.example.backend.event.IssueExpiredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class IssueEventListener {

    @EventListener
    public void handleIssueExpired(IssueExpiredEvent event) {
        System.out.println("⚠️ L'issue avec ID " + event.getIssue().getId() +
                " a dépassé sa date d’échéance : " + event.getIssue().getDate_echeance());
    }
}

