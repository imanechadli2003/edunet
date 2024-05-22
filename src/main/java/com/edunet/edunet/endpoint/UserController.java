package com.edunet.edunet.endpoint;

import com.edunet.edunet.dto.*;
import com.edunet.edunet.model.User;
import com.edunet.edunet.service.PostService;
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
@CrossOrigin(origins = "*")
public class UserController {

    private UserService userService;

    private final PostService postService;

    @GetMapping("/{id}")
    public GetUserRequest getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/signup")
    public GetUserRequest createNewUser(@RequestBody PostUserRequest data) {
        System.out.println("Signing up " + data.handle());
        return userService.save(data);
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

    @GetMapping("/{id}/posts/public")
    public List<GetPostRequest> getUserPublicPosts(@PathVariable long id) {
        return postService.getUserPublicPosts(id);
    }

    @GetMapping("/{id}/posts/private")
    public List<GetPostRequest> getUserPrivatePosts(@PathVariable long id) {
        return postService.getUserPrivatePosts(id);
    }

    @PostMapping("/posts/public")
    public GetPostRequest createUserPublicPost(@RequestBody PostPostRequest data) {
        return postService.createUserPublicPost(data);
    }

    @PostMapping("/posts/private")
    public GetPostRequest createUserPrivatePost(@RequestBody PostPostRequest data) {
        return postService.createUserPrivatePost(data);
    }
}
