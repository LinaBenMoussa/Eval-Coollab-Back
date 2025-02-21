package com.example.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.backend.service.ParametreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtDecoder {

    @Autowired
    private ParametreService parametreService;
    @Autowired
    private JwtProperties jwtProperties;

    public DecodedJWT decode(String token){
          return JWT.require(Algorithm.HMAC256(parametreService.getJwtSecret()))
                  .build().verify(token);
    }
}
