package com.edunet.edunet.service;

import com.edunet.edunet.dto.GetTopicRequest;
import com.edunet.edunet.dto.MembershipRequest;
import com.edunet.edunet.dto.MembershipRequestResponse;
import com.edunet.edunet.dto.PostTopicRequest;
import com.edunet.edunet.model.Topic;
import com.edunet.edunet.model.TopicMembership;
import com.edunet.edunet.model.TopicMembershipRequest;
import com.edunet.edunet.model.User;
import com.edunet.edunet.repository.MembershipRepository;
import com.edunet.edunet.repository.MembershipRequestRepository;
import com.edunet.edunet.repository.TopicRepository;
import com.edunet.edunet.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    private final MembershipRequestRepository membershipRequestRepository;

    private final MembershipRepository topicMembershipRepository;

    public GetTopicRequest createTopic(PostTopicRequest data) {
        // TODO - Reiterate
        Topic topic = new Topic();
        topic.setName(data.name());
        topic.setDescription(data.description());
        topic.setPrivacy(Topic.Privacy.fromInt(data.privacy()));
        String ownerHandle = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findUserByHandle(ownerHandle).get();
        topic.setOwner(owner);
        topic.setCreatedOn(LocalDate.now());
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

    public void deleteTopic(int id) {
        Optional<String> ownerHandle = topicRepository.findHandleOfOwner(id);
        if (ownerHandle.isEmpty()) {
            throw new IllegalArgumentException("Topic not found");
        }
        String authHandle = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!ownerHandle.get().equals(authHandle)) {
            throw new IllegalArgumentException("Not an owner of the resource");
        }
        topicRepository.deleteById(id);
    }

    public GetTopicRequest updateTopic(int id, PostTopicRequest data) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
        topicRepository.save(postTopicRequestToRequest(topic, data));
        return topicToGetTopicRequest(topic);
    }

    public void addMembershipRequest(MembershipRequest data) {
        Topic topic = topicRepository.findTopicByName(data.topicName())
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
        User user = userRepository.findUserByHandle(data.handle())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        TopicMembershipRequest request = new TopicMembershipRequest();
        request.setTopic(topic);
        request.setUser(user);
        membershipRequestRepository.save(request);
    }

    public void respondToMembershipRequest(MembershipRequestResponse data) {
        // TODO - Reiterate
        TopicMembershipRequest request = membershipRequestRepository.findById(data.requestId())
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        String authHandle = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!authHandle.equals(request.getTopic().getOwner().getHandle())) {
            throw new IllegalArgumentException("not authorized to modify this request");
        }
        if (data.accepted()) {
            TopicMembership membership = new TopicMembership();
            membership.setTopic(request.getTopic());
            membership.setUser(request.getUser());
            membership.setPermission(TopicMembership.Permission.fromInt(data.permission()));
            topicMembershipRepository.save(membership);
        }
        membershipRequestRepository.delete(request);
    }

    public List<MembershipRequest> getAllRequests(int id) {
        // TODO - Reiterate
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
        String authHandle = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!authHandle.equals(topic.getOwner().getHandle())) {
            throw new IllegalArgumentException("Not authorized to see this information");
        }
        return membershipRequestRepository.findHandlesByTopicId(id).stream()
                .map(handle -> new MembershipRequest(topic.getName(), handle))
                .toList();
    }

    public List<GetTopicRequest> getAllTopics() {
        // TODO - Which topics to show
        return topicRepository.findAll().stream()
                .map(TopicService::topicToGetTopicRequest)
                .toList();
    }

    private static Topic postTopicRequestToRequest(Topic topic, PostTopicRequest data) {
        topic.setDescription(data.description());
        topic.setName(data.name());
        return topic;
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
