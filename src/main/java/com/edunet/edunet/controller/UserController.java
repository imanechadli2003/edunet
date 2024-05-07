package com.edunet.edunet.controller;

import com.edunet.edunet.dto.CreateUserRequest;
import com.edunet.edunet.models.User;
import com.edunet.edunet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/users", produces = "application/json")
@AllArgsConstructor
public class UserController {

    private UserService userService;


    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        Optional<User> user =  userService.findUserById(id);
        return user.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
    }

    @PostMapping("/signup")
    public void createNewUser(@RequestBody CreateUserRequest userRequest) {
        userService.save(userRequest);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        // TODO
        return null;
    }

    @PostMapping("/{id}")
    public User updateUser(@PathVariable int id) {
        // TODO
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        // TODO
    }

    @GetMapping("/search")
    public List<User> search() {
        // TODO
        return null;
    }
}
