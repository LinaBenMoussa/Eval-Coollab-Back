package com.example.backend.service;

import com.example.backend.dto.NotificationRequestDto;
import com.example.backend.entity.Notification;
import com.example.backend.entity.User;
import com.example.backend.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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


}
