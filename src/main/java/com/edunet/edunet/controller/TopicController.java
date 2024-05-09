package com.edunet.edunet.controller;


import com.edunet.edunet.dto.GetTopicRequest;
import com.edunet.edunet.service.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
}
