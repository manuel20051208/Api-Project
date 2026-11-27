package com.apiproject.services;

import com.apiproject.DTOs.Auth.ChangePasswordRequestDTO;
import com.apiproject.DTOs.Client.UpdateClientRequestDTO;
import com.apiproject.entities.client.UserClient;
import com.apiproject.repositories.client.ClientRepository;
import com.apiproject.services.client.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** PATCH /client/modify sin contrasena y PATCH /client/change-password exigiendo la vigente. */
@ExtendWith(MockitoExtension.class)
class ClientServiceAccountTest {

    private static final long ID = 10L;
    private static final String HASH = "$2a$10$hashActual";

    @Mock private ClientRepository clientRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private ClientService clientService;

    private UserClient cliente(String password) {
        UserClient c = new UserClient();
        c.setId(ID);
        c.setFullName("Cliente");
        c.setEmail("cliente@test.com");
        c.setPassword(password);
        return c;
    }

    private HttpStatus statusOf(Runnable call) {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, call::run);
        return HttpStatus.valueOf(ex.getStatusCode().value());
    }

    // ---------- modify ----------

    @Test
    void modify_noTocaLaContrasena() {
        UserClient c = cliente(HASH);
        when(clientRepository.findById(ID)).thenReturn(Optional.of(c));

        clientService.modifyData(ID, new UpdateClientRequestDTO("Nuevo Nombre", null, null, null, null));

        assertEquals("Nuevo Nombre", c.getFullName());
        assertEquals(HASH, c.getPassword());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void modify_nombreVacio_400() {
        when(clientRepository.findById(ID)).thenReturn(Optional.of(cliente(HASH)));

        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(() -> clientService.modifyData(ID, new UpdateClientRequestDTO("  ", null, null, null, null))));
        verify(clientRepository, never()).save(any());
    }

    // ---------- change-password ----------

    @Test
    void changePassword_actualCorrecta_guardaHashNuevo() {
        when(clientRepository.findById(ID)).thenReturn(Optional.of(cliente(HASH)));
        when(passwordEncoder.matches("actual123", HASH)).thenReturn(true);
        when(passwordEncoder.matches("nueva12345", HASH)).thenReturn(false);
        when(passwordEncoder.encode("nueva12345")).thenReturn("$2a$10$hashNuevo");

        clientService.changePassword(ID, new ChangePasswordRequestDTO("actual123", "nueva12345"));

        verify(clientRepository).updatePasswordById(ID, "$2a$10$hashNuevo");
    }

    @Test
    void changePassword_actualIncorrecta_400YNoGuarda() {
        when(clientRepository.findById(ID)).thenReturn(Optional.of(cliente(HASH)));
        when(passwordEncoder.matches("mala", HASH)).thenReturn(false);

        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(() -> clientService.changePassword(ID, new ChangePasswordRequestDTO("mala", "nueva12345"))));
        verify(clientRepository, never()).updatePasswordById(anyLong(), anyString());
    }

    @Test
    void changePassword_nuevaCorta_400SinConsultarBD() {
        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(() -> clientService.changePassword(ID, new ChangePasswordRequestDTO("actual123", "corta"))));
        verifyNoInteractions(clientRepository);
    }

    @Test
    void changePassword_nuevaIgualALaActual_400() {
        when(clientRepository.findById(ID)).thenReturn(Optional.of(cliente(HASH)));
        when(passwordEncoder.matches("misma12345", HASH)).thenReturn(true);

        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(() -> clientService.changePassword(ID, new ChangePasswordRequestDTO("misma12345", "misma12345"))));
        verify(clientRepository, never()).updatePasswordById(anyLong(), anyString());
    }

    @Test
    void changePassword_cuentaGoogleSinContrasena_400() {
        when(clientRepository.findById(ID)).thenReturn(Optional.of(cliente(null)));

        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(() -> clientService.changePassword(ID, new ChangePasswordRequestDTO("algo1234", "nueva12345"))));
        verify(clientRepository, never()).updatePasswordById(anyLong(), anyString());
    }
}
