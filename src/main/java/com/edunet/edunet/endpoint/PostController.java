package com.edunet.edunet.endpoint;


import com.edunet.edunet.dto.CommentDto;
import com.edunet.edunet.dto.PostDto;
import com.edunet.edunet.dto.CreatePostDto;
import com.edunet.edunet.dto.Vote;
import com.edunet.edunet.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/posts", produces = "application/json")
@AllArgsConstructor
@CrossOrigin(methods = {RequestMethod.OPTIONS, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.HEAD, RequestMethod.PUT, RequestMethod.POST}, origins = "*")
public class PostController {

    private final PostService postService;

    @GetMapping
    public List<PostDto> getAllPosts(@RequestParam String topic, @RequestParam int page, @RequestParam int size) {
        return postService.getPosts(topic, page, size);
    }

    @PostMapping("/{id}")
    public PostDto updatePost(int id, @RequestBody CreatePostDto data) {
        return postService.updatePost(id, data);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable int id) {
        this.postService.deletePost(id);
    }

    @PostMapping("/vote/{id}")
    public void vote(@PathVariable int id, @RequestBody Vote vote) {
        postService.vote(id, vote);
    }

    @PostMapping("/{id}/comments")
    public CommentDto addComment(@PathVariable int id, @RequestBody CommentDto comment) {
        return this.postService.addComment(id, comment);
    }

    @GetMapping("/{id}/comments")
    List<CommentDto> getComments(@PathVariable int id) {
        return  this.postService.getComments(id);
    }

}
