package com.edunet.edunet.service;

import com.edunet.edunet.dto.GetTopicRequest;
import com.edunet.edunet.dto.MembershipRequestResponse;
import com.edunet.edunet.dto.PostTopicRequest;
import com.edunet.edunet.dto.UserIdHandle;
import com.edunet.edunet.exception.ApplicationError;
import com.edunet.edunet.exception.BadRequestException;
import com.edunet.edunet.exception.NotAllowedException;
import com.edunet.edunet.exception.ResourceNotFoundException;
import com.edunet.edunet.model.Topic;
import com.edunet.edunet.model.TopicMembership;
import com.edunet.edunet.model.TopicMembershipRequest;
import com.edunet.edunet.model.User;
import com.edunet.edunet.repository.MembershipRepository;
import com.edunet.edunet.repository.MembershipRequestRepository;
import com.edunet.edunet.repository.TopicRepository;
import com.edunet.edunet.repository.UserRepository;
import com.edunet.edunet.security.AuthenticationService;
import static com.edunet.edunet.model.TopicMembership.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    private final MembershipRequestRepository membershipRequestRepository;

    private final MembershipRepository topicMembershipRepository;

    private final AuthenticationService authService;

    public GetTopicRequest createTopic(PostTopicRequest data) {
        Topic topic = new Topic();
        topic.setName(data.name());
        topic.setDescription(data.description());
        topic.setPrivacy(Topic.Privacy.fromInt(data.privacy()));
        topic.setOwner(new User(authService.getAuthenticatedUserId()));
        topic.setCreatedOn(LocalDate.now());
        topicRepository.save(topic);
        TopicMembership ownership = new TopicMembership();
        ownership.setUser(new User(authService.getAuthenticatedUserId()));
        ownership.setTopic(topic);
        ownership.setPermission(Permission.OWNER);
        topicMembershipRepository.save(ownership);
        return TopicService.topicToGetTopicRequest(topic);
    }

    public GetTopicRequest getTopic(int id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("topic " + id));
        return topicToGetTopicRequest(topic);
    }

    public void deleteTopic(int id) {
        checkIfAuthenticatedUserIsOwner(id);
        topicRepository.deleteById(id);
    }

    public GetTopicRequest updateTopic(int id, PostTopicRequest data) {
        Topic topic = getTopicIfAuthenticatedIsOwner(id);
        topicRepository.save(postTopicRequestToRequest(topic, data));
        return topicToGetTopicRequest(topic);
    }

    public void addMembershipRequest(int topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("topic " + topicId));
        User user = userRepository.findUserById(authService.getAuthenticatedUserId())
                .orElseThrow(() -> new ApplicationError("unexpected error"));
        TopicMembershipRequest request = new TopicMembershipRequest();
        request.setTopic(topic);
        request.setUser(user);
        membershipRequestRepository.save(request);
    }

    public void respondToMembershipRequest(MembershipRequestResponse data) {
        TopicMembershipRequest request = membershipRequestRepository.findById(data.requestId())
                .orElseThrow(() -> new ResourceNotFoundException("request " + data.requestId()));
        checkIfAuthenticatedUserIsOwner(request.getTopic().getId());
        if (data.accepted()) {
            TopicMembership membership = new TopicMembership();
            membership.setTopic(request.getTopic());
            membership.setUser(request.getUser());
            int val = data.permission();
            if (!Permission.isValid(val)) {
                throw new BadRequestException("invalid permission value:" + val);
            }
            Permission permission = Permission.fromInt(val);
            membership.setPermission(permission);
            topicMembershipRepository.save(membership);
        }
        membershipRequestRepository.delete(request);
    }

    public List<UserIdHandle> getAllRequests(int id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("topic " + id));
        if (authService.getAuthenticatedUserId() != topic.getOwner().getId()) {
            throw new NotAllowedException("not enough permissions");
        }
        return membershipRequestRepository.findHandlesByTopicId(id).stream()
                .map(handle -> new UserIdHandle(null, handle))
                .toList();
    }

    public List<GetTopicRequest> getAllTopics(int size, int page) {
        PageRequest pr = PageRequest.of(page, size);
        return topicRepository.findByType(Topic.TopicType.CREATED_TOPIC).stream()
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

    private void checkIfAuthenticatedUserIsOwner(int topicId) {
        long ownerId = topicRepository.findOwnerIdById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("topic: " + topicId));
        long authId = authService.getAuthenticatedUserId();
        if (authId != ownerId) {
            throw new NotAllowedException("not enough permissions");
        }
    }

    private Topic getTopicIfAuthenticatedIsOwner(int topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("topic: " + topicId));
        long authId = authService.getAuthenticatedUserId();
        if (authId != topic.getOwner().getId()) {
            throw new NotAllowedException("not enough permissions");
        }
        return topic;
    }
}
