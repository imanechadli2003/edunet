package com.edunet.edunet.controller;


import com.edunet.edunet.dto.GetTopicRequest;
import com.edunet.edunet.dto.MembershipRequest;
import com.edunet.edunet.dto.MembershipRequestResponse;
import com.edunet.edunet.dto.PostTopicRequest;
import com.edunet.edunet.service.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/topics", produces = "application/json")
public class TopicController {

    private final TopicService topicService;

    @GetMapping("/{id}")
    public GetTopicRequest getTopic(@PathVariable int id) {
        return topicService.getTopic(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
    }

    @PostMapping()
    public GetTopicRequest createTopic(@RequestBody PostTopicRequest data) {
        return topicService.createTopic(data);
    }

    @PostMapping("/{id}")
    public GetTopicRequest updateTopic(@PathVariable int id, PostTopicRequest data) {
        return topicService.updateTopic(id, data);
    }

    @DeleteMapping("/{id}")
    public void deleteTopic(@PathVariable int id) {
        topicService.deleteTopic(id);
    }

    @PostMapping("/request")
    public void requestMembership(@RequestBody MembershipRequest data) {
        topicService.addMembershipRequest(data);
    }

    @GetMapping("/{id}/requests")
    public List<MembershipRequest> getAllRequests(@PathVariable int id) {
        return topicService.getAllRequests(id);
    }

    @PostMapping("/responses")
    public void respondToMembershipRequest(@RequestBody MembershipRequestResponse data) {
        topicService.respondToMembershipRequest(data);
    }

    // TODO - Update user membership [update permissions, remove]
}
