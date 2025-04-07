package com.example.backend.controller;

import com.example.backend.dto.request.FeedbackRequestDto;
import com.example.backend.dto.response.FeedbackResponseDto;
import com.example.backend.entity.Feedback;
import com.example.backend.service.FeedbackService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/feedback")
@AllArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

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

    @GetMapping("/filtre")
    public ResponseEntity<FeedbackResponseDto> filtreFeedback(
            @RequestParam(required = false) Long collaborateurId,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        FeedbackResponseDto response = feedbackService.filtreFeedback(
                collaborateurId,
                managerId,
                type,
                startDate, endDate,
                offset, limit);

        return ResponseEntity.ok(response);
    }
}
