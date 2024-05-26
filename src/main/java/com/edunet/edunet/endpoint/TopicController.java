package com.edunet.edunet.endpoint;


import com.edunet.edunet.dto.*;
import com.edunet.edunet.service.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/topics", produces = "application/json")
@CrossOrigin(origins = "*")
public class TopicController {

    private final TopicService topicService;

    @GetMapping("/{id}")
    public TopicDto getTopic(@PathVariable int id) {
        return topicService.getTopic(id);
    }

    @PostMapping
    public TopicDto createTopic(@RequestBody CreateTopicDto data) {
        return topicService.createTopic(data);
    }

    @PostMapping("/{id}")
    public TopicDto updateTopic(@PathVariable int id, CreateTopicDto data) {
        return topicService.updateTopic(id, data);
    }

    @DeleteMapping("/{id}")
    public void deleteTopic(@PathVariable int id) {
        topicService.deleteTopic(id);
    }

    @PostMapping("/{id}/join")
    public void requestMembership(@PathVariable int id) {
        topicService.addMembershipRequest(id);
    }

    @GetMapping("/{id}/requests")
    public List<UserIdHandle> getAllRequests(@PathVariable int id) {
        return topicService.getAllRequests(id);
    }

    @PostMapping("/responses")
    public void respondToMembershipRequest(@RequestBody MembershipRequestResponse data) {
        topicService.respondToMembershipRequest(data);
    }

    @GetMapping
    public List<TopicDto> getTopics(@RequestParam int page, @RequestParam int size) {
        return topicService.getAllTopicsForUser(page, size);
    }

    @GetMapping("/search")
    public List<TopicDto> search(@RequestParam String like, @RequestParam int page, @RequestParam int size) {
        return this.topicService.search(like, page, size);
    }

    @GetMapping("/{id}/posts")
    public List<PostDto> getPosts(@PathVariable int id) {
        return topicService.getPosts(id);
    }

    @PostMapping("/{id}/posts")
    public PostDto createPost(@PathVariable int id, @RequestBody CreatePostDto data) {
        return topicService.createPost(id, data);
    }

    @GetMapping("/{id}/membership")
    public MembershipDto getMembership(@PathVariable int id) {
        return topicService.membershipOfAuthUser(id);
    }


    // TODO - Update user membership [update permissions, remove]
}
