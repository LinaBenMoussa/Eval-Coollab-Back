package com.example.backend.controller;

import com.example.backend.dto.request.LoginRequestDto;
import com.example.backend.dto.response.LoginResponseDto;
import com.example.backend.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(("/auth"))
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Validated LoginRequestDto request) {
        return ResponseEntity.ok(authService.attemptLogin(request.getUsername(), request.getPassword()));
    }
}