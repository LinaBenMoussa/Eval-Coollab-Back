package com.example.backend.job;

import com.example.backend.entity.Issue;
import com.example.backend.event.IssueExpiredEvent;
import com.example.backend.repository.IssueRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class IssueScheduler {
    private final IssueRepository issueRepository;
    private final ApplicationEventPublisher eventPublisher;

    public IssueScheduler(IssueRepository issueRepository, ApplicationEventPublisher eventPublisher) {
        this.issueRepository = issueRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedRate = 60000)
    public void checkExpiredIssues() {
        LocalDateTime now = LocalDateTime.now();
        List<Issue> expiredIssues = issueRepository.findNonExpiredIssues(now);

        for (Issue issue : expiredIssues) {
            System.out.println("Issue expirée trouvée : " + issue.getId());
            eventPublisher.publishEvent(new IssueExpiredEvent(issue));

            issue.setExpired(true);
            issueRepository.save(issue);
        }
    }
}

