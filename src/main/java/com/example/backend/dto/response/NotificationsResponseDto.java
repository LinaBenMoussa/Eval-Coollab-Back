package com.example.backend.dto.response;

import com.example.backend.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NotificationsResponseDto {
    private List<Notification> notifications;
    private long total;
}
