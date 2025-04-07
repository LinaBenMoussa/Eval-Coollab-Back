package com.example.backend.service;

import com.example.backend.dto.request.FeedbackRequestDto;
import com.example.backend.dto.response.FeedbackResponseDto;
import com.example.backend.entity.Feedback;
import com.example.backend.entity.User;
import com.example.backend.exception.ApplicationException;
import com.example.backend.repository.FeedbackRepository;
import com.example.backend.specification.FeedbackSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
@Service
public class FeedbackService {
    private final UserService userService;
    private final FeedbackRepository feedbackRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

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
        feedback.setDate_feedback(LocalDateTime.now());

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

    public List<Feedback> getFeedbackByManagerAndCollaborateur(Long managerId, Long collaborateurId) {
        return feedbackRepository.findByManagerIdAndCollaborateurId(managerId, collaborateurId);
    }


    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    public FeedbackResponseDto filtreFeedback(
            Long collaborateurId,
            Long managerId,
            String type,
            LocalDate startDate,
            LocalDate endDate,
            int offset,
            int limit) {

        Specification<Feedback> spec = Specification.where(null);

        if (collaborateurId != null) {
            spec = spec.and(FeedbackSpecifications.hasCollaborateurId(collaborateurId));
        }

        if (managerId != null) {
            spec = spec.and(FeedbackSpecifications.hasManagerId(managerId));
        }

        if (type != null && !type.isEmpty()) {
            spec = spec.and(FeedbackSpecifications.hasType(type));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and(FeedbackSpecifications.isBetweenDatesFeedback(startDate, endDate));
        }

        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Feedback> result = feedbackRepository.findAll(spec, pageable);

        return new FeedbackResponseDto(result.getContent(), result.getTotalElements());
    }
}
