package com.example.backend.controller;

import com.example.backend.dto.ParametreDto;
import com.example.backend.service.ParametreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parametres")
public class ParametreController {

    @Autowired
    private ParametreService parametreService;

    @GetMapping
    public ResponseEntity<ParametreDto> getParametre() {
        Long expiration = parametreService.getJwtExpiration();

        ParametreDto responseDto = new ParametreDto(expiration);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping
    public ResponseEntity<String> setParametre(@RequestBody ParametreDto param) {
        parametreService.setJwtExpiration(param.getJwt_expiration());
        return ResponseEntity.ok("Clé JWT mise à jour");
    }



}

