package com.example.backend.service;

import com.example.backend.dto.FeedbackRequestDto;
import com.example.backend.entity.Feedback;
import com.example.backend.entity.User;
import com.example.backend.exception.ApplicationException;
import com.example.backend.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private UserService userService;
    @Autowired
    private FeedbackRepository feedbackRepository;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public Feedback createFeedback(FeedbackRequestDto feedbackDto) {
        if (feedbackDto.getCommentaire() == null || feedbackDto.getCommentaire().isEmpty() ||
                feedbackDto.getType() == null || feedbackDto.getType().isEmpty() ||
                feedbackDto.getCollaborateurId() == null) {

            throw new ApplicationException("Veuillez remplir tous les champs.");
        }

        User collaborateur = userService.getUserById(feedbackDto.getCollaborateurId())
                .orElseThrow(() -> new RuntimeException("Collaborateur introuvable !"));
        User manager = userService.getUserById(feedbackDto.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager introuvable !"));


        Feedback feedback = new Feedback();
        feedback.setCommentaire(feedbackDto.getCommentaire());
        feedback.setCollaborateur(collaborateur);
        feedback.setManager(manager);
        feedback.setType(feedbackDto.getType());
        feedback.setDateFeedback(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id).orElse(null);
    }

    public List<Feedback> getFeedbackByIdCollaborateur(Long collaborateurId) {
       return feedbackRepository.findByCollaborateurId(collaborateurId);
    }

    public List<Feedback> getFeedbackByIdManager(Long managerId) {
        return feedbackRepository.findByManagerId(managerId);
    }

    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}
