package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvaliException;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserController {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public User createNewUser(@RequestBody User userinput) {
        String hashPassword = this.passwordEncoder.encode(userinput.getPassword());
        userinput.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(userinput);
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
    public void deletUser(@PathVariable("id") long id) throws IdInvaliException {
        if (id > 1500) {
            throw new IdInvaliException("ID qua to");
        }
        this.userService.handleDeleteUser(id);
    }

}
