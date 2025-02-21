package com.example.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.backend.entity.Parametre;
import com.example.backend.entity.User;
import com.example.backend.service.ParametreService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Component
public class JwtIssuer {
    @Autowired
    private ParametreService parametreService;
    @Autowired
    private JwtProperties jwtProperties;
    public String issue(long userId, String email, List<String> role){
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(Instant.now().plus(Duration.of(parametreService.getJwtExpiration(), ChronoUnit.DAYS)))
                .withClaim("username",email)
                .withClaim("role",role)
                .sign(Algorithm.HMAC256(parametreService.getJwtSecret()));
    }
}
