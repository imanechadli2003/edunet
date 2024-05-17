package com.edunet.edunet.endpoint;

import com.edunet.edunet.dto.Login;
import com.edunet.edunet.dto.PostUserRequest;
import com.edunet.edunet.dto.GetUserRequest;
import com.edunet.edunet.dto.UpdatePasswordRequest;
import com.edunet.edunet.model.User;
import com.edunet.edunet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/api/users", produces = "application/json")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/{id}")
    public GetUserRequest getUser(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @PostMapping("/signup")
    public void createNewUser(@RequestBody PostUserRequest data) {
        userService.save(data);
    }

    @GetMapping("/all")
    public List<GetUserRequest> getAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.getAllUsers();
    }

    @PostMapping("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody PostUserRequest data) {
        userService.updateUser(id, data);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/search")
    public List<User> search() {
        // TODO
        return Collections.emptyList();
    }

    @PostMapping("/update-password/{id}")
    public void updatePassword(@PathVariable Long id, @RequestBody UpdatePasswordRequest password) {
        userService.updatePassword(id, password);
    }

    @PostMapping("/login")
    public void login(@RequestBody Login login) {
        // TODO
    }

    @GetMapping("/logout")
    public void logout() {
        // TODO
    }
}
