package com.example.backend.dto.response;

import com.example.backend.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Builder
@Getter
@Setter
public class LoginResponseDto {
    private String accessToken;
    private List<String> role;
    private String firstName;
    private String lastName;
    private String matricule;

    private Long id;
}
