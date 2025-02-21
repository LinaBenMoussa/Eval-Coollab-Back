package com.example.backend.controller;

import com.example.backend.service.ParametreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parametres")
public class ParametreController {

    @Autowired
    private ParametreService parametreService;

    @GetMapping("/jwt-secret")
    public ResponseEntity<String> getJwtSecret() {
        String secret = parametreService.getJwtSecret();
        return ResponseEntity.ok(secret);
    }

    @PutMapping("/jwt-secret")
    public ResponseEntity<String> setJwtSecret(@RequestBody String jwtSecret) {
        parametreService.setJwtSecret(jwtSecret);
        return ResponseEntity.ok("Clé JWT mise à jour");
    }

    @GetMapping("/jwt-expiration")
    public ResponseEntity<Long> getJwtExpiration() {
        long expiration = parametreService.getJwtExpiration();
        return ResponseEntity.ok(expiration);
    }

    @PutMapping("/jwt-expiration")
    public ResponseEntity<String> setJwtExpiration(@RequestBody long expiration) {
        parametreService.setJwtExpiration(expiration);
        return ResponseEntity.ok("Expiration JWT mise à jour");
    }
}

