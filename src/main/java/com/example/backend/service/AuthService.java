package com.example.backend.service;

import com.example.backend.dto.response.LoginResponseDto;
import com.example.backend.entity.User;
import com.example.backend.exception.ApplicationException;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtIssuer;
import com.example.backend.security.UserPrinciple;
import lombok.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Data
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtIssuer jwtIssuer;

    public LoginResponseDto attemptLogin(String email, String password) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            var principle = (UserPrinciple) authentication.getPrincipal();

            System.out.println("email" + principle);

            var roles = principle.getAuthorities().stream()
                    .map(authority -> "ROLE_" + authority.getAuthority().toUpperCase())
                    .toList();

            var token = jwtIssuer.issue(principle.getId(), principle.getUsername(), roles);

            Optional<User> user = userRepository.findById(principle.getId());

            return LoginResponseDto.builder()
                    .id(principle.getId())
                    .accessToken(token)
                    .firstName(user.get().getPrenom())
                    .lastName(user.get().getNom())
                    .role(roles)
                    .build();
        } catch (AuthenticationException e) {
            throw new ApplicationException("Échec de l'authentification : email ou mot de passe incorrect.");
        }
    }


}
