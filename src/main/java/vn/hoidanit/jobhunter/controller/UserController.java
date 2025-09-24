package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.anotation.APIMessage;
import vn.hoidanit.jobhunter.util.error.IdInvaliException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @APIMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User userinput) throws IdInvaliException {
        boolean isEmailExist = this.userService.isEmailExist(userinput.getEmail());

        if (isEmailExist) {
            throw new IdInvaliException("Email " + userinput.getEmail() + " is exist, please input another email!");
        }

        String hashPassword = this.passwordEncoder.encode(userinput.getPassword());
        userinput.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(userinput);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.ConvertToResCreateDTO(newUser));
    }

    @GetMapping("/users")
    @APIMessage("Fetch all user")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.userService.handleGetAllUser(spec, pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUserByID(@PathVariable("id") Long id) throws IdInvaliException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvaliException("User with ID = " + id + " do not exist!");
        }
        return ResponseEntity.ok(this.userService.ConvertToResUserDTO(currentUser));
    }

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvaliException {
        User updateUser = this.userService.handleUpdateUser(user);
        if (updateUser == null) {
            throw new IdInvaliException("User with ID = " + user.getId() + " do not exist!");
        }
        return ResponseEntity.ok().body(this.userService.ConvertToResUpdateUserDTO(updateUser));
    }

    @DeleteMapping("/users/{id}")
    @APIMessage("Delete a user")
    public ResponseEntity<Void> deletUser(@PathVariable("id") long id) throws IdInvaliException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvaliException("User with ID = " + id + " do not exist!");
        }

        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

}
