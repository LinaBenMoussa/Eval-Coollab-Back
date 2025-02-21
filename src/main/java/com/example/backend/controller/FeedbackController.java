package com.example.backend.controller;

import com.example.backend.dto.FeedbackRequestDto;
import com.example.backend.entity.Feedback;
import com.example.backend.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/create")
    public Feedback createFeedback(@RequestBody FeedbackRequestDto feedbackDto) {
        return feedbackService.createFeedback(feedbackDto);
    }

    @GetMapping("/collaborateur/{id}")
    public List<Feedback> getFeedbackByCollaborateurId(@PathVariable Long id){
        return feedbackService.getFeedbackByIdCollaborateur(id);
    }

    @GetMapping("/manager/{id}")
    public List<Feedback> getFeedbackByManager(@PathVariable Long id){
        return feedbackService.getFeedbackByIdManager(id);
    }
}
