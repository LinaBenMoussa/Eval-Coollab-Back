package com.example.backend.dto.response;

import com.example.backend.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDto {
    private List<Feedback> feedbacks;
    private long totalElements;
}
