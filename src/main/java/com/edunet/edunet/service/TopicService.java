package com.edunet.edunet.service;

import com.edunet.edunet.dto.GetTopicRequest;
import com.edunet.edunet.dto.PostTopicRequest;
import com.edunet.edunet.model.Topic;
import com.edunet.edunet.repository.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    public GetTopicRequest save(PostTopicRequest data) {
        Topic topic = new Topic();
        topic.setName(data.name());
        topic.setDescription(data.description());
        topic.setPrivacy(Topic.Privacy.fromInt(data.privacy()));
        int id = 2; // TODO - Get id of authenticated user
        topicRepository.save(topic);
        return TopicService.topicToGetTopicRequest(topic);
    }

    public Optional<GetTopicRequest> getTopic(int id) {
        Optional<Topic> topic = topicRepository.findById(id);
        if (topic.isEmpty()) {
            throw new IllegalArgumentException("Topic not found");
        }
        return topic.map(TopicService::topicToGetTopicRequest);
    }

    public List<GetTopicRequest> getAllTopics() {
        // TODO - Which topics to show
        return topicRepository.findAll().stream()
                .map(TopicService::topicToGetTopicRequest)
                .toList();
    }

    private static GetTopicRequest topicToGetTopicRequest(Topic topic) {
        return new GetTopicRequest(
                topic.getName(),
                topic.getDescription(),
                topic.getPrivacy().name(),
                topic.getOwner().getHandle(),
                topic.getCreatedOn());
    }
}
