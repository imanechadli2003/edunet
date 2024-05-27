package com.edunet.edunet.endpoint;

import com.edunet.edunet.dto.*;
import com.edunet.edunet.model.User;
import com.edunet.edunet.service.PostService;
import com.edunet.edunet.service.TopicService;
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

    private final TopicService topicService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/signup")
    public UserDto createNewUser(@RequestBody CreateUserDto data) {
        System.out.println("Signing up " + data.handle());
        return userService.save(data);
    }

    @GetMapping("/all")
    public List<UserDto> getAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.getAllUsers();
    }

    @PostMapping("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody CreateUserDto data) {
        userService.updateUser(id, data);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/change-password/{id}")
    public void updatePassword(@PathVariable Long id, @RequestBody UpdatePasswordDto password) {
        userService.updatePassword(id, password);
    }

    @GetMapping("/{id}/posts/public")
    public List<PostDto> getUserPublicPosts(@PathVariable long id) {
        return userService.getUserPublicPosts(id);
    }

    @GetMapping("/{id}/posts/private")
    public List<PostDto> getUserPrivatePosts(@PathVariable long id) {
        return userService.getUserPrivatePosts(id);
    }

    @GetMapping("/search")
    public List<UserDto> search(@RequestParam String like, @RequestParam int page, @RequestParam int size) {
        return this.userService.search(like, page, size);
    }

    @PostMapping("/posts/public")
    public PostDto createUserPublicPost(@RequestBody CreatePostDto data) {
        return userService.createUserPublicPost(data);
    }

    @PostMapping("/posts/private")
    public PostDto createUserPrivatePost(@RequestBody CreatePostDto data) {
        // TODO - return postService.user(data);
        return null;
    }
}
