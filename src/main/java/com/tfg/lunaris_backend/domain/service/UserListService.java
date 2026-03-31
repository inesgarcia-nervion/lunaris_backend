package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.UserListRepository;
import com.tfg.lunaris_backend.domain.model.UserList;
import com.tfg.lunaris_backend.presentation.exceptions.UserListNotFoundException;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class UserListService {

    @Autowired
    private UserListRepository userListRepository;

    // GET
    public List<UserList> getAllUserLists() {
        return userListRepository.findAll();
    }

    // GET - paginated
    public Page<UserList> getAllUserLists(Pageable pageable) {
        return userListRepository.findAll(pageable);
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
