package com.edunet.edunet.endpoint;


import com.edunet.edunet.dto.GetPostRequest;
import com.edunet.edunet.dto.PostPostRequest;
import com.edunet.edunet.dto.UpdatePostRequest;
import com.edunet.edunet.dto.Vote;
import com.edunet.edunet.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/posts", produces = "application/json")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public GetPostRequest createNewPost(@RequestBody PostPostRequest data) {
        return postService.createNewPost(data);
    }

    @GetMapping
    public List<GetPostRequest> getAllPosts(@RequestParam String topic, @RequestParam int page, @RequestParam int size) {
        return postService.getPosts(topic, page, size);
    }

    @PostMapping("/{id}")
    public GetPostRequest updatePost(int id, @RequestBody UpdatePostRequest data) {
        return postService.updatePost(id, data);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable int postId) {
        this.postService.deletePost(postId);
    }

    @PostMapping("/vote/{id}")
    public void vote(@PathVariable int id, @RequestBody Vote vote) {
        postService.vote(id, vote);
    }
}
