package com.example.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestDto {
    private Long id;
    private String nom;
    private String prenom;
    private String username;
    private String password;
    private String role;
    private Long managerId;
    private Long id_redmine;
    private Long id_bitrix24;

}
