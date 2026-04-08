package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.tfg.lunaris_backend.data.repository.UserRepository;
import com.tfg.lunaris_backend.domain.model.User;
import com.tfg.lunaris_backend.presentation.exceptions.UserNotFoundException;

import java.util.List;

/**
 * Servicio que maneja la lógica de negocio relacionada con los usuarios.
 * 
 * Proporciona métodos para crear, actualizar, eliminar y obtener usuarios.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtiene todos los usuarios.
     * @return lista de usuarios
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Obtiene un usuario por su ID.
     * @param id ID del usuario
     * @return usuario encontrado
     * @throws UserNotFoundException si no se encuentra el usuario con el id proporcionado
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id " + id));
    }

    /**
     * Crea un nuevo usuario.
     * @param user objeto con los datos del usuario a crear
     * @return usuario creado
     */
    public User createUser(User user) {
        if (user.getUsername() != null && userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    /**
     * Actualiza un usuario existente.
     * @param id ID del usuario a actualizar
     * @param userDetails detalles del usuario a actualizar
     * @return usuario actualizado
     * @throws UserNotFoundException si no se encuentra el usuario con el id proporcionado
     */
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id " + id));
        if (userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
            user.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        return userRepository.save(user);
    }

    /**
     * Elimina un usuario por su ID.
     * @param id ID del usuario a eliminar
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Actualiza un usuario existente por su nombre de usuario.
     * @param username nombre de usuario del usuario a actualizar
     * @param userDetails detalles del usuario a actualizar
     * @return usuario actualizado
     * @throws UserNotFoundException si no se encuentra el usuario con el nombre de usuario proporcionado
     */
    public User updateUserByUsername(String username, User userDetails) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con username " + username));
        if (userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
            user.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        return userRepository.save(user);
    }
}
