package com.tfg.lunaris_backend.data.config;

import com.tfg.lunaris_backend.data.repository.GenreRepository;
import com.tfg.lunaris_backend.data.repository.UserRepository;
import com.tfg.lunaris_backend.domain.model.Genre;
import com.tfg.lunaris_backend.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Inicializador de datos para entornos de desarrollo.
 * 
 * Inserta un usuario administrador por defecto y una lista de géneros si la base
 * de datos está vacía.
 */
@Component
public class DataSeeder implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GenreRepository genreRepository;

    /**
     * Ejecuta la semilla de datos al arrancar la aplicación.
     *  
     * @param args argumentos de la aplicación (no usados)
     */
    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@lunaris.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }

        if (genreRepository.count() == 0) {
            List<String> genreNames = List.of(
                    "Fantasía", "Ciencia Ficción", "Romance", "Misterio", "Thriller",
                    "Aventura", "Histórico", "Ficción", "Terror", "Drama",
                    "Comedia", "Biografía", "Autoayuda", "Infantil", "Juvenil",
                    "Poesía", "Filosofía", "Ciencia", "Historia", "Tecnología");
            genreNames.forEach(name -> {
                Genre genre = new Genre();
                genre.setName(name);
                genreRepository.save(genre);
            });
        }
    }
}
