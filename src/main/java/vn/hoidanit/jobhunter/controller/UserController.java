package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User createNewUser(@RequestBody User user) {
        User newUser = this.userService.handleCreateUser(user);
        return newUser;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        List<User> listUsers = this.userService.handleGetAllUser();
        return listUsers;
    }

    @GetMapping("/users/{id}")
    public User getUserByID(@PathVariable("id") Long id) {
        User userResult = this.userService.fetchUserById(id);
        return userResult;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        User userUpdate = this.userService.handleUpdateUser(user);
        return userUpdate;
    }

    @DeleteMapping("/users/{id}")
    public void deletUser(@PathVariable("id") long id) {
        this.userService.handleDeleteUser(id);
    }

}
