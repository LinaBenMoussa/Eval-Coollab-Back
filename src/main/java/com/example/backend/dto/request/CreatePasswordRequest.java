package com.example.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePasswordRequest {
    private String username;
    private String password;
}
