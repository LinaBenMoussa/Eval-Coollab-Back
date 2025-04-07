package com.example.backend.controller;

import com.example.backend.dto.request.BadgeRequest;
import com.example.backend.entity.EmployeBadge;
import com.example.backend.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/badge")
public class BadgeController {
    private final BadgeService badgeService;

    @Autowired
    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @PostMapping("/attribuerBadge")
    public String attribuerBadge(@RequestBody BadgeRequest request){
        badgeService.attribuerBadges(request.getMois());
        return "badges attribuers";
    }

    @GetMapping("/collaborateur/{id}")
    public List<EmployeBadge> getBadgeByCollaborateur(@PathVariable Long id){
        return badgeService.getBadgesByCollaborateur(id);
    }
}
