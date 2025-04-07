package com.example.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class LoginResponseDto {
    private String accessToken;
    private List<String> role;
    private String user;
    private Long id;
}
