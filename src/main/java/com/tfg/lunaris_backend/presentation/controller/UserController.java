package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.domain.model.User;
import com.tfg.lunaris_backend.domain.service.UserService;

import java.util.List;

/**
 * Controlador que maneja las operaciones relacionadas con los usuarios.
 * 
 * Proporciona endpoints para crear, obtener, actualizar y eliminar usuarios.
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Endpoint para obtener todas las usuarios. Devuelve una lista de todas las
     * usuarios disponibles.
     * 
     * @return lista de usuarios
     */
    @GetMapping("/")
    public String home() {
        return "Hola desde LunarisBackend!";
    }

    /**
     * Endpoint para obtener todas las usuarios. Devuelve una lista de todas las
     * usuarios disponibles.
     * 
     * @return lista de usuarios
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Endpoint para obtener un usuario por su ID. Devuelve el usuario
     * correspondiente si existe.
     * 
     * @param id identificador del usuario
     * @return usuario encontrado
     */
    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * Endpoint para crear un nuevo usuario. Recibe un objeto con los datos del
     * usuario a crear y
     * devuelve el usuario creado.
     * 
     * @param user objeto con los datos del usuario a crear
     * @return usuario creado
     */
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * Endpoint para actualizar un usuario existente. Recibe el ID del usuario a
     * actualizar
     * y un objeto con los datos a actualizar, y devuelve el usuario actualizado.
     * 
     * @param id          identificador del usuario a actualizar
     * @param userDetails objeto con los datos del usuario a actualizar
     * @return usuario actualizado
     */
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userService.updateUser(id, userDetails);
    }

    /**
     * Endpoint para actualizar un usuario existente por su nombre de usuario.
     * Recibe
     * el nombre de usuario del usuario a actualizar y un objeto con los datos a
     * actualizar,
     * y devuelve el usuario actualizado.
     * 
     * @param username    nombre de usuario del usuario a actualizar
     * @param userDetails objeto con los datos del usuario a actualizar
     * @return usuario actualizado
     */
    @PutMapping("/users/username/{username}")
    public User updateUserByUsername(@PathVariable String username, @RequestBody User userDetails) {
        return userService.updateUserByUsername(username, userDetails);
    }

    @GetMapping("/users/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    /**
     * Endpoint para actualizar únicamente el avatar de un usuario.
     * Acepta el contenido del avatar como text/plain en el body (por ejemplo
     * una data URL) para evitar problemas de mapeo con objetos grandes.
     */
    @PostMapping(path = "/users/username/{username}/avatar", consumes = "text/plain")
    public User updateAvatarByUsername(@PathVariable String username, @RequestBody String avatarData) {
        User u = new User();
        u.setAvatarUrl(avatarData);
        return userService.updateUserByUsername(username, u);
    }

    /**
     * Endpoint para eliminar un usuario por su ID. Recibe el ID del usuario a
     * eliminar
     * y elimina el usuario correspondiente.
     * 
     * @param id identificador del usuario a eliminar
     */
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
