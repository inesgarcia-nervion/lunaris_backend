package com.tfg.lunaris_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.exceptions.UserListNotFoundException;
import com.tfg.lunaris_backend.model.UserList;
import com.tfg.lunaris_backend.repository.UserListRepository;
import java.util.List;

@Service
public class UserListService {

    @Autowired
    private UserListRepository userListRepository;

    // GET
    public List<UserList> getAllUserLists() {
        return userListRepository.findAll();
    }

    // GET BY ID
    public UserList getUserListById(Long id) {
        return userListRepository.findById(id)
                .orElseThrow(() -> new UserListNotFoundException("Lista no encontrada con id " + id));
    }

    // CREATE (POST)
    public UserList createUserList(UserList userList) {
        return userListRepository.save(userList);
    }

    // UPDATE
    public UserList updateUserList(Long id, UserList userListDetails) {
        UserList userList = userListRepository.findById(id)
                .orElseThrow(() -> new UserListNotFoundException("Lista no encontrada con id " + id));
        userList.setName(userListDetails.getName());
        return userListRepository.save(userList);
    }

    // DELETE
    public void deleteUserList(Long id) {
        userListRepository.deleteById(id);
    }
}
