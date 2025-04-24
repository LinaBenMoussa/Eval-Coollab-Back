package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.exception.ApplicationException;
import com.example.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User createUser(String nom, String prenom,String matricule, String username, String password, String role, Long managerId,Long id_redmine,Long id_bitrix24) {

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
        user.setId_redmine(id_redmine);
        user.setId_bitrix24(id_bitrix24);
        user.setMatricule(matricule);

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

    public User editUser(Long id, String nom, String prenom, String username, String password, String role, Long managerId,Long id_bitrix24, Long id_redmine ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Utilisateur non trouvé."));

        if (nom != null && !nom.isEmpty()) user.setNom(nom);
        if (prenom != null && !prenom.isEmpty()) user.setPrenom(prenom);
        if (username != null && !username.isEmpty()) {
            if (!username.equals(user.getUsername()) && userRepository.findByUsername(username).isPresent()) {
                throw new ApplicationException("Le nom d'utilisateur '" + username + "' existe déjà.");
            }
            user.setUsername(username);
        }
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        if (role != null && !role.isEmpty()) user.setRole(role);
        user.setId_redmine(id_redmine);
        user.setId_bitrix24(id_bitrix24);

        if ("COLLABORATEUR".equalsIgnoreCase(role)) {
            if (managerId == null) {
                throw new ApplicationException("Un collaborateur doit obligatoirement avoir un manager.");
            }
            User manager = userRepository.findById(managerId)
                    .orElseThrow(() -> new ApplicationException("Le manager avec l'ID " + managerId + " n'existe pas."));
            user.setManager(manager);
        } else {
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
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new EntityNotFoundException("Utilisateur non trouvé avec l'id : " + id);
        }
        User user = userOptional.get();

        if ("manager".equalsIgnoreCase(user.getRole())) {
            List<User> collaborateurs = userRepository.findByManagerId(id);
            for (User collaborateur : collaborateurs) {
                collaborateur.setManager(null);
            }
            userRepository.saveAll(collaborateurs);
        }

        userRepository.delete(user);
    }

    public User createPassword(String username, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new ApplicationException("Le mot de passe ne peut pas être vide.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("Utilisateur non trouvé."));

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            throw new ApplicationException("Le mot de passe a déjà été défini.");
        }

        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }
    public boolean isNewUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("Utilisateur non trouvé."));

        return user.getPassword() == null || user.getPassword().isEmpty();
    }
    public boolean existeUser(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

}
