package com.example.backend.dto;

import com.example.backend.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestDto {

    private String nom;
    private String prenom;
    private String username;
    private String password;
    private String role;
    private Long managerId;

}
