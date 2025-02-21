package com.example.backend.controller;

import com.example.backend.dto.IssueRequestDto;
import com.example.backend.entity.Issue;
import com.example.backend.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;


    @PostMapping
    public Issue createIssue(@RequestBody IssueRequestDto issue) {
        return issueService.createIssue(issue);
    }

    @GetMapping("/manager/{managerId}")
    public List<Issue> getIssuesByManagerId(@PathVariable Long managerId) {
        return issueService.getIssuesByManagerId(managerId);
    }
}

