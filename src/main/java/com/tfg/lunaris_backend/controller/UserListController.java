package com.tfg.lunaris_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.model.UserList;
import com.tfg.lunaris_backend.repository.UserListRepository;
import java.util.List;

@RestController
public class UserListController {
    @Autowired
    private UserListRepository userlistRepository;

    @GetMapping("/")
    public String home() {
        return "Hola desde LunarisBackend!";
    }

    @GetMapping("/user_list")
    public List<UserList> getAllUsers() {
        return userlistRepository.findAll();
    }
}
