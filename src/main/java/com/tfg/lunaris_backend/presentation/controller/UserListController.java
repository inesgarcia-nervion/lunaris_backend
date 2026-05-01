package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tfg.lunaris_backend.domain.model.UserList;
import com.tfg.lunaris_backend.domain.service.UserListService;

/**
 * Controlador que maneja las operaciones relacionadas con las listas de
 * usuarios.
 * 
 * Proporciona endpoints para crear, obtener, actualizar y eliminar listas de
 * usuarios.
 */
@RestController
public class UserListController {
    @Autowired
    private UserListService userListService;

    /**
     * Endpoint para obtener todas las listas de usuarios. Devuelve una página de
     * listas de usuarios disponibles.
     * 
     * @param pageable información de paginación
     * @return página de listas de usuarios
     */
    @GetMapping("/user_list")
    public Page<UserList> getAllUserLists(Pageable pageable) {
        return userListService.getAllUserLists(pageable);
    }

    @GetMapping("/user_list/owner/{owner}")
    public java.util.List<UserList> getListsByOwner(@PathVariable String owner) {
        return userListService.getListsByOwner(owner);
    }

    /**
     * Endpoint para obtener una lista de usuarios por su ID. Devuelve la lista de
     * usuarios correspondiente si existe.
     * 
     * @param id identificador de la lista de usuarios
     * @return lista de usuarios encontrada
     */
    @GetMapping("/user_list/{id}")
    public UserList getUserListById(@PathVariable Long id) {
        return userListService.getUserListById(id);
    }

    /**
     * Endpoint para crear una nueva lista de usuarios. Recibe un objeto con los
     * datos
     * de la lista de usuarios a crear y devuelve la lista de usuarios creada.
     * 
     * @param userList objeto con los datos de la lista de usuarios a crear
     * @return lista de usuarios creada
     */
    @PostMapping("/user_list")
    public UserList createUserList(@RequestBody UserList userList) {
        return userListService.createUserList(userList);
    }

    /**
     * Endpoint para actualizar una lista de usuarios existente. Recibe el ID de la
     * lista de usuarios a actualizar
     * y un objeto con los datos a actualizar, y devuelve la lista de usuarios
     * actualizada.
     * 
     * @param id              identificador de la lista de usuarios a actualizar
     * @param userListDetails objeto con los datos de la lista de usuarios a
     *                        actualizar
     * @return lista de usuarios actualizada
     */
    @PutMapping("/user_list/{id}")
    public UserList updateUserList(@PathVariable Long id, @RequestBody UserList userListDetails) {
        return userListService.updateUserList(id, userListDetails);
    }

    /**
     * Endpoint para eliminar una lista de usuarios por su ID. Recibe el ID de la
     * lista de usuarios a eliminar
     * y elimina la lista de usuarios correspondiente.
     * 
     * @param id identificador de la lista de usuarios a eliminar
     */
    @DeleteMapping("/user_list/{id}")
    public void deleteUserList(@PathVariable Long id) {
        userListService.deleteUserList(id);
    }
}
