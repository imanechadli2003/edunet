package com.edunet.edunet.service;

import com.edunet.edunet.model.Topic;
import com.edunet.edunet.repository.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    public void save(Topic topic) {
        topicRepository.save(topic);
    }
}
