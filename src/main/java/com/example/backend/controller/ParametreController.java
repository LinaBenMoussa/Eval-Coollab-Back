package com.example.backend.controller;

import com.example.backend.dto.response.ParametreDto;
import com.example.backend.entity.Parametre;
import com.example.backend.service.ParametreService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parametres")
@AllArgsConstructor
public class ParametreController {

    private final ParametreService parametreService;

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

    @PutMapping("/jwt-secret")
    public ResponseEntity<String> setSecretKey(@RequestBody String secret) {
        parametreService.setJwtSecret(secret);
        return ResponseEntity.ok("Clé JWT mise à jour");
    }

    @PutMapping("/jwt-expiration")
    public ResponseEntity<String> setExpiration(@RequestBody Long expiration) {
        parametreService.setJwtExpiration(expiration);
        return ResponseEntity.ok("Clé JWT mise à jour");
    }

    @GetMapping("/{cle}")
    public ResponseEntity<String> getParametre(@PathVariable String cle) {
        String valeur = parametreService.getParametre(cle);
        if (valeur != null) {
            return ResponseEntity.ok(valeur);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{cle}")
    public ResponseEntity<String> updateParametre(@PathVariable String cle, @RequestBody String valeur) {
        parametreService.setParametre(cle, valeur);
        return ResponseEntity.ok("Paramètre mis à jour avec succès.");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Parametre>> getAll(){
      return ResponseEntity.ok(parametreService.getAll());
    }

}

