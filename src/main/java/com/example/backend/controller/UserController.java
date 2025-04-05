package com.example.backend.controller;

import com.example.backend.dto.CreateUserRequestDto;
import com.example.backend.entity.User;
import com.example.backend.service.CollaborateurStatsService;
import com.example.backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(("/users"))
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @Autowired
    private CollaborateurStatsService collaborateurService;

    @PostMapping("/create")
    public User createUser(@RequestBody CreateUserRequestDto user) {
        return userService.createUser(user.getNom(), user.getPrenom(),user.getUsername() ,user.getPassword(),user.getRole(),user.getManagerId(), user.getId_redmine(), user.getId_redmine());
    }
    @PutMapping
    public User updateUser(@RequestBody CreateUserRequestDto user) {
        return userService.editUser(user.getId(),user.getNom(), user.getPrenom(),user.getUsername() ,user.getPassword(),user.getRole(),user.getManagerId(), user.getId_bitrix24(), user.getId_redmine());
    }
    @GetMapping("/byrole/{role}")
    public List<User> getCollaborators(@PathVariable String role) {
        return userService.getByRole(role);
    }

    @GetMapping("/collaborateursByManager/{id}")
    public List<User> getCollaboratorsByIdManager(@PathVariable Long id) {
        return userService.getCollaboratorsByIdManager(id);
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès");
    }

    @GetMapping("/collaborateur/stats/{collaborateurId}")
    public Map<String, Object> getCollaborateurStats(
            @PathVariable Long collaborateurId,
            @RequestParam("type") String type,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        java.sql.Date sqlStartDate = java.sql.Date.valueOf(startDate);
        java.sql.Date sqlEndDate = java.sql.Date.valueOf(endDate);

        return collaborateurService.getCollaborateurStats(collaborateurId, sqlStartDate, sqlEndDate,type);
    }

}
