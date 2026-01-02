package com.tfg.lunaris_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.model.UserList;
import com.tfg.lunaris_backend.service.UserListService;
import java.util.List;

@RestController
public class UserListController {
    @Autowired
    private UserListService userListService;

    @GetMapping("/user_list")
    public List<UserList> getAllUserLists() {
        return userListService.getAllUserLists();
    }

    @GetMapping("/user_list/{id}")
    public UserList getUserListById(@PathVariable Long id) {
        return userListService.getUserListById(id);
    }

    @PostMapping("/user_list")
    public UserList createUserList(@RequestBody UserList userList) {
        return userListService.createUserList(userList);
    }

    @PutMapping("/user_list/{id}")
    public UserList updateUserList(@PathVariable Long id, @RequestBody UserList userListDetails) {
        return userListService.updateUserList(id, userListDetails);
    }

    @DeleteMapping("/user_list/{id}")
    public void deleteUserList(@PathVariable Long id) {
        userListService.deleteUserList(id);
    }
}
