package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.UserListRepository;
import com.tfg.lunaris_backend.domain.model.UserList;
import com.tfg.lunaris_backend.presentation.exceptions.UserListNotFoundException;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio que maneja la lógica de negocio relacionada con las listas de
 * usuarios.
 * 
 * Proporciona métodos para crear, actualizar, eliminar y obtener listas de
 * usuarios.
 */
@Service
public class UserListService {

    @Autowired
    private UserListRepository userListRepository;

    /**
     * Obtiene todas las listas de usuarios.
     * 
     * @return lista de listas de usuarios
     */
    public List<UserList> getAllUserLists() {
        return userListRepository.findAll();
    }

    /**
     * Obtiene todas las listas de usuarios de manera paginada.
     * 
     * @param pageable información de paginación
     * @return página de listas de usuarios
     */
    public Page<UserList> getAllUserLists(Pageable pageable) {
        return userListRepository.findAll(pageable);
    }

    /**
     * Obtiene una lista de usuarios por su ID.
     * 
     * @param id ID de la lista de usuarios
     * @return lista de usuarios encontrada
     * @throws UserListNotFoundException si la lista de usuarios no existe
     */
    public UserList getUserListById(Long id) {
        return userListRepository.findById(id)
                .orElseThrow(() -> new UserListNotFoundException("Lista no encontrada con id " + id));
    }

    /**
     * Crea una nueva lista de usuarios.
     * 
     * @param userList lista de usuarios a crear
     * @return lista de usuarios creada
     */
    public UserList createUserList(UserList userList) {
        return userListRepository.save(userList);
    }

    /**
     * Actualiza una lista de usuarios existente.
     * 
     * @param id              ID de la lista de usuarios a actualizar
     * @param userListDetails detalles de la lista de usuarios a actualizar
     * @return lista de usuarios actualizada
     * @throws UserListNotFoundException si la lista de usuarios no existe
     */
    public UserList updateUserList(Long id, UserList userListDetails) {
        UserList userList = userListRepository.findById(id)
                .orElseThrow(() -> new UserListNotFoundException("Lista no encontrada con id " + id));
        userList.setName(userListDetails.getName());
        if (userListDetails.getOwner() != null)
            userList.setOwner(userListDetails.getOwner());
        if (userListDetails.getIsPrivate() != null)
            userList.setIsPrivate(userListDetails.getIsPrivate());
        if (userListDetails.getBooksJson() != null)
            userList.setBooksJson(userListDetails.getBooksJson());
        return userListRepository.save(userList);
    }

    /**
     * Elimina una lista de usuarios por su ID.
     * 
     * @param id ID de la lista de usuarios a eliminar
     */
    public void deleteUserList(Long id) {
        userListRepository.deleteById(id);
    }

    public java.util.List<UserList> getListsByOwner(String owner) {
        return userListRepository.findByOwner(owner);
    }
}
