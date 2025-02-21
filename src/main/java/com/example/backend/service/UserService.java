package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.exception.ApplicationException;
import com.example.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User createUser(String nom, String prenom, String username, String password, String role, Long managerId) {

        if (nom == null || nom.isEmpty() ||
                prenom == null || prenom.isEmpty() ||
                username == null || username.isEmpty() ||
                password == null || password.isEmpty() ||
                role == null || role.isEmpty()) {
            throw new ApplicationException("Veuillez remplir tous les champs.");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new ApplicationException("Le nom d'utilisateur '" + username + "' existe déjà.");
        }

        User user = new User();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setUsername(username);
        user.setRole(role);

        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);

        if ("COLLABORATEUR".equalsIgnoreCase(role)) {
            if (managerId == null) {
                throw new ApplicationException("Un collaborateur doit obligatoirement avoir un manager.");
            }
            User manager = userRepository.findById(managerId)
                    .orElseThrow(() -> new ApplicationException("Le manager avec l'ID " + managerId + " n'existe pas."));
            user.setManager(manager);
        } else {

            if (managerId != null) {
                throw new ApplicationException("Un manager ou un admin ne peut pas avoir de manager.");
            }
            user.setManager(null);
        }

        return userRepository.save(user);
    }



    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getByRole(String role) {
        return userRepository.findByRole(role);
    }


    public List<User> getCollaboratorsByIdManager(Long managerId) {
        return userRepository.findByRoleAndManagerId("Collaborateur", managerId);
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
