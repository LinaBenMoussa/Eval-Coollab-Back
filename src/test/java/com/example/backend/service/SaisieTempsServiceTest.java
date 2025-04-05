package com.example.backend.service;

import com.example.backend.dto.NotificationRequestDto;
import com.example.backend.entity.SaisieTemps;
import com.example.backend.entity.User;
import com.example.backend.repository.SaisieTempsRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SaisieTempsServiceTest {

    @Mock
    private SaisieTempsRepository saisieTempsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private SaisieTempsService saisieTempsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckSaisieTemps_WhenNoSaisie_ShouldSendNotificationAndEmail() {
        // GIVEN : Un utilisateur sans saisie de temps
        LocalDate date = LocalDate.now();
        User collaborateur = new User();
        collaborateur.setId(1L);
        collaborateur.setEmail("collaborateur@example.com");
        collaborateur.setId_bitrix24(123L);

        List<User> collaborateurs = List.of(collaborateur);
        when(userRepository.findByRole("Collaborateur")).thenReturn(collaborateurs);
        when(saisieTempsRepository.findByCollaborateurAndDate(collaborateur, date)).thenReturn(Collections.emptyList());

        // WHEN : Exécution de la méthode
        saisieTempsService.checkSaisieTemps(date);

        // THEN : Vérifier l'envoi d'une notification
        ArgumentCaptor<NotificationRequestDto> notificationCaptor = ArgumentCaptor.forClass(NotificationRequestDto.class);
        verify(notificationService, times(1)).createNotification(notificationCaptor.capture());

        NotificationRequestDto notificationEnvoyee = notificationCaptor.getValue();
        assertEquals("Rappel de saisie de temps", notificationEnvoyee.getSujet());
        assertEquals("Vous n'avez pas effectué de saisie de temps pour le " + date + ".", notificationEnvoyee.getContenu());
        assertEquals(collaborateur.getId(), notificationEnvoyee.getCollaborateur_id());

        // Vérifier l'envoi de l'email
        verify(emailService, times(1)).sendEmail(eq("lina.b.moussa@gmail.com"), eq("Rappel : Saisie de temps manquante"), anyString());
    }

    @Test
    void testCheckSaisieTemps_WhenSaisieExists_ShouldNotSendNotificationOrEmail() {
        LocalDate date = LocalDate.now();
        User collaborateur = new User();
        collaborateur.setId(1L);
        collaborateur.setEmail("collaborateur@example.com");

        List<User> collaborateurs = List.of(collaborateur);
        when(userRepository.findByRole("Collaborateur")).thenReturn(collaborateurs);
        when(saisieTempsRepository.findByCollaborateurAndDate(collaborateur, date)).thenReturn(List.of(new SaisieTemps()));

        saisieTempsService.checkSaisieTemps(date);

        verify(notificationService, never()).createNotification(any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}
