package com.tfg.lunaris_backend.data.config;

import com.tfg.lunaris_backend.data.repository.GenreRepository;
import com.tfg.lunaris_backend.data.repository.UserRepository;
import com.tfg.lunaris_backend.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test para {@link DataSeeder}.
 */
class DataSeederTest {

    private DataSeeder seeder;
    private UserRepository userRepo;
    private GenreRepository genreRepo;
    private PasswordEncoder passwordEncoder;

    /**
     * Configura el entorno de pruebas, creando mocks y asignándolos al seeder mediante reflexión.
     * @throws Exception si ocurre un error al acceder a los campos privados
     */
    @BeforeEach
    void setUp() throws Exception {
        seeder = new DataSeeder();
        userRepo = mock(UserRepository.class);
        genreRepo = mock(GenreRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        Field ur = DataSeeder.class.getDeclaredField("userRepository");
        ur.setAccessible(true);
        ur.set(seeder, userRepo);

        Field gr = DataSeeder.class.getDeclaredField("genreRepository");
        gr.setAccessible(true);
        gr.set(seeder, genreRepo);

        Field pe = DataSeeder.class.getDeclaredField("passwordEncoder");
        pe.setAccessible(true);
        pe.set(seeder, passwordEncoder);
    }

    /**
     * Verifica que el método run guarda el usuario admin y los géneros cuando no existen.
     */
    @Test
    void run_savesAdminAndGenres_whenMissing() {
        when(userRepo.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(genreRepo.count()).thenReturn(0L);

        seeder.run(null);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo, times(1)).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertEquals("admin", saved.getUsername());
        assertEquals("admin@lunaris.com", saved.getEmail());
        assertEquals("encoded", saved.getPassword());

        verify(genreRepo, atLeastOnce()).save(any());
    }

    /**
     * Verifica que el método run no realiza ninguna acción cuando el usuario admin ya existe y los géneros están presentes.
     */
    @Test
    void run_doesNothing_whenAdminExistsAndGenresPresent() {
        User existing = new User();
        existing.setUsername("admin");
        when(userRepo.findByUsername("admin")).thenReturn(Optional.of(existing));
        when(genreRepo.count()).thenReturn(5L);

        seeder.run(null);

        verify(userRepo, never()).save(any());
        verify(genreRepo, never()).save(any());
    }
}
