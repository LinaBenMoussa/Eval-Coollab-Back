package com.example.backend.service;

import com.example.backend.dto.request.NotificationRequestDto;
import com.example.backend.dto.response.NotificationsResponseDto;
import com.example.backend.entity.Issue;
import com.example.backend.entity.Notification;
import com.example.backend.entity.User;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.specification.NotificationSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class NotificationService {
    private final UserService userService;
    private final NotificationRepository notificationRepository;

    public Notification createNotification(NotificationRequestDto request) {
        User collaborateur = userService.getUserById(request.getCollaborateur_id())
                .orElseThrow(() -> new RuntimeException("Collaborateur introuvable !"));
        Notification notification = new Notification();
        notification.setSujet(request.getSujet());
        notification.setContenu(request.getContenu());
        notification.setCollaborateur(collaborateur);
        notification.setDateEnvoi(request.getDateEnvoi());

        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationByIdCollaborateur(Long id){
        return notificationRepository.findByCollaborateur_IdOrderByCreatedDateDesc(id);
    }
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification introuvable avec l'ID : " + id));
        notificationRepository.delete(notification);
    }

    public void markNotificationsAsRead(List<Long> notificationIds) {
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);
        for (Notification notification : notifications) {
            notification.setLu(true);
        }
        notificationRepository.saveAll(notifications);
    }

    public List<Notification> getNotificationByIdManager(Long id){
        return notificationRepository.findByCollaborateur_ManagerIdOrderByCreatedDateDesc(id);
    }

    public NotificationsResponseDto getFiltredNotifications(
            Long managerId,
            Long collaborateurId,
            LocalDate startDate,
            LocalDate endDate,
            int offset,
            int limit
    ){
        Specification<Notification> spec = Specification.where(null);

        if (managerId != null) {
            spec = spec.and(NotificationSpecifications.hasManagerId(managerId));
        }

        // Filtre par collaborateur
        if (collaborateurId != null) {
            spec = spec.and(NotificationSpecifications.hasCollaborateurId(collaborateurId));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and(NotificationSpecifications.isBetweenDate(
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59, 59)));
        }
        int page = offset / limit;
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "dateEnvoi"));

        Page<Notification> result = notificationRepository.findAll(spec, pageable);

        return new NotificationsResponseDto(result.getContent(), result.getTotalElements());
    }

}
