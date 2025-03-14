package com.example.backend.service;

import com.example.backend.dto.NotificationRequestDto;
import com.example.backend.entity.Notification;
import com.example.backend.entity.User;
import com.example.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private UserService userService;

    @Autowired
    private NotificationRepository notificationRepository;

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
        return notificationRepository.findByCollaborateur_Id(id);
    }

}
