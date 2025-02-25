package com.example.backend.controller;

import com.example.backend.dto.CreateUserRequestDto;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;

@RestController
@RequestMapping(("/users"))
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public User createUser(@RequestBody CreateUserRequestDto user) {
        return userService.createUser(user.getNom(), user.getPrenom(),user.getUsername() ,user.getPassword(),user.getRole(),user.getManagerId(), user.getId_redmine(), user.getId_redmine());
    }
    @GetMapping("/byrole/{role}")
    public List<User> getCollaborators(@PathVariable String role) {
        return userService.getByRole(role);
    }

    @GetMapping("/collaborateursByManager/{id}")
    public List<User> getCollaboratorsByIdManager(@PathVariable Long id) {
        return userService.getCollaboratorsByIdManager(id);
    }
}
