package com.example.backend.job;

import com.example.backend.entity.Issue;
import com.example.backend.event.IssueExpiredEvent;
import com.example.backend.repository.IssueRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Component
public class IssueScheduler {
    private final IssueRepository issueRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Scheduled(cron = "0 0 11,16 * * *")
    public void checkExpiredIssues() {
        LocalDateTime now = LocalDateTime.now();
        List<Issue> expiredIssues = issueRepository.findNonExpiredIssues(now);

        for (Issue issue : expiredIssues) {
            eventPublisher.publishEvent(new IssueExpiredEvent(issue));

            issue.setExpired(true);
            issueRepository.save(issue);
        }
    }
}

