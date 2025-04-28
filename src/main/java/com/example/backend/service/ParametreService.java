package com.example.backend.service;

import com.example.backend.entity.Parametre;
import com.example.backend.repository.ParametreRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ParametreService {

    private final ParametreRepository parametreRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Parametre> getParametre() {
        return parametreRepository.findAll();
    }

    public String getJwtSecret() {
        return parametreRepository.findByCle("jwt_secret_key")
                .map(Parametre::getValeur)
                .orElseThrow(() -> new RuntimeException("jwt_secret_key non défini en base !"));
    }

    public void setJwtSecret(String jwtSecret) {
        String hashedSecret = passwordEncoder.encode(jwtSecret);

        Parametre existingParam = parametreRepository.findByCle("jwt_secret_key").orElse(null);

        if (existingParam != null) {
            existingParam.setValeur(hashedSecret);
            parametreRepository.save(existingParam);
        } else {
            Parametre newParam = new Parametre();
            newParam.setCle("jwt_secret_key");
            newParam.setValeur(hashedSecret);
            parametreRepository.save(newParam);
        }
    }

    public long getJwtExpiration() {
        return parametreRepository.findByCle("jwt_expiration")
                .map(param -> Long.parseLong(param.getValeur()))
                .orElseThrow(() -> new RuntimeException("jwt_expiration non défini en base !"));
    }

    public void setJwtExpiration(long expiration) {
        Parametre existingParam = parametreRepository.findByCle("jwt_expiration").orElse(null);
        if (existingParam != null) {
            existingParam.setValeur(String.valueOf(expiration));
            parametreRepository.save(existingParam);}
        else {
            Parametre newParam = new Parametre();
            newParam.setCle("jwt_expiration");
            newParam.setValeur(String.valueOf(expiration));
            parametreRepository.save(newParam);
        }
    }
    public String getParametre(String cle) {
        return parametreRepository.findByCle(cle)
                .map(Parametre::getValeur)
                .orElse(null);
    }

    public void setParametre(String cle, String valeur) {
        // Cherche le paramètre existant
        Parametre parametre = parametreRepository.findByCle(cle)
                .orElseThrow(() -> new IllegalArgumentException("Le paramètre avec la clé " + cle + " n'existe pas"));

        // Met à jour la valeur du paramètre
        parametre.setValeur(valeur);

        // Sauvegarde le paramètre mis à jour
        parametreRepository.save(parametre);
    }

    public List<Parametre> getAll() {
        return parametreRepository.findAll().stream()
                .filter(parametre -> !"jwt_secret_key".equals(parametre.getCle()))
                .collect(Collectors.toList());
    }



}

