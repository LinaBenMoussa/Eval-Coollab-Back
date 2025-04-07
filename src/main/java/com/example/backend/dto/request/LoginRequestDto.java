package com.example.backend.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequestDto {
    private String username;
    private String password;
    private String role;
}
