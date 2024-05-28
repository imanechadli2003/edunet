package com.edunet.edunet.service;

import com.edunet.edunet.dto.*;
import com.edunet.edunet.exception.ApplicationError;
import com.edunet.edunet.exception.BadRequestException;
import com.edunet.edunet.exception.NotAllowedException;
import com.edunet.edunet.exception.ResourceNotFoundException;
import com.edunet.edunet.model.*;
import com.edunet.edunet.repository.*;
import com.edunet.edunet.security.AuthenticationService;
import static com.edunet.edunet.model.TopicMembership.*;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    private final MembershipRequestRepository membershipRequestRepository;

    private final MembershipRepository topicMembershipRepository;

    private final AuthenticationService authService;

    private final PostRepository postRepository;

    public TopicDto createTopic(CreateTopicDto data) {
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

    public TopicDto getTopic(int id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("topic " + id));

        return topicToGetTopicRequest(topic);
    }

    public void deleteTopic(int id) {
        checkIfAuthenticatedUserIsOwner(id);
        topicRepository.deleteById(id);
    }

    public TopicDto updateTopic(int id, CreateTopicDto data) {
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
        try {
            membershipRequestRepository.save(request);
        } catch (DataIntegrityViolationException exc) {
            throw new NotAllowedException("This request is already added");
        }
    }

    public void respondToMembershipRequest(MembershipRequestResponse data) {
        TopicMembershipRequest request = membershipRequestRepository.findById(data.requestId())
                .orElseThrow(() -> new ResourceNotFoundException("request " + data.requestId()));
        checkIfAuthenticatedUserIsOwner(request.getTopic().getId());
        if (data.accepted()) {
            TopicMembership membership = new TopicMembership();
            membership.setTopic(request.getTopic());
            membership.setUser(request.getUser());
            Permission permission = Permission.WRITE;
            membership.setPermission(permission);
            topicMembershipRepository.save(membership);
        }
        membershipRequestRepository.delete(request);
    }

    public List<JoinRequest> getAllRequests(int id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("topic " + id));
        if (authService.getAuthenticatedUserId() != topic.getOwner().getId()) {
            throw new NotAllowedException("not enough permissions");
        }
        return membershipRequestRepository.findRequestUser(id).stream()
                .map(result -> new JoinRequest((int) result[0], (long) result[1], (String) result[2]))
                .toList();
    }

    public List<TopicDto> getAllTopics(int size, int page) {
        PageRequest pr = PageRequest.of(page, size);
        return topicRepository.findByType(Topic.TopicType.CREATED_TOPIC).stream()
                .map(TopicService::topicToGetTopicRequest)
                .toList();
    }

    private static Topic postTopicRequestToRequest(Topic topic, CreateTopicDto data) {
        topic.setDescription(data.description());
        topic.setName(data.name());
        return topic;
    }

    private static TopicDto topicToGetTopicRequest(Topic topic) {
        return new TopicDto(
                topic.getId(),
                topic.getName(),
                topic.getDescription(),
                topic.getPrivacy().name(),
                topic.getOwner().getHandle(),
                topic.getCreatedOn(),
                "");
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

    public List<TopicDto> getAllTopicsForUser(int page, int size) {
        long id = authService.getAuthenticatedUserId();
        PageRequest pr = PageRequest.of(page, size);
        return topicMembershipRepository.findTopicsForUserById(id, Topic.TopicType.CREATED_TOPIC, pr)
                .stream().map(TopicService::topicToGetTopicRequest)
                .toList();
    }

    public List<TopicDto> search(String like, int page, int size) {
        PageRequest pr = PageRequest.of(page, size);
        return topicRepository.findByNameContainingAndType(like, Topic.TopicType.CREATED_TOPIC, pr).stream()
                .map(TopicService::topicToGetTopicRequest)
                .toList();
    }

    public List<PostDto> getPosts(int topicId) {
        Topic.Privacy privacy = topicRepository.findPrivacyById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("topic " + topicId));
        if (privacy == Topic.Privacy.PRIVATE) {
            long userId = authService.getAuthenticatedUserId();
            if (!isMemberOfTopic(userId, topicId)) {
                throw new NotAllowedException("Not Authorized");
            }
        }
        PageRequest pr = PageRequest.of(0, 10);
        return postRepository.findAllById(topicId, pr).stream()
                .map(TopicService::postToGetPostRequest)
                .toList();
    }

    private boolean isMemberOfTopic(long userId, int topicId) {
        return topicMembershipRepository.findPermissionById(userId, topicId).isPresent();
    }

    public MembershipDto membershipOfAuthUser(int topicId) {
        long userId = authService.getAuthenticatedUserId();
        return topicMembershipRepository.findPermissionById(userId, topicId)
                .map(permission -> new MembershipDto(
                        permission.name()))
                .orElseGet(() -> new MembershipDto("NONE"));
    }

    private static PostDto postToGetPostRequest(Post post) {
        return new PostDto(
                post.getId(),
                post.getTopic().getName(),
                post.getAuthor().getHandle(),
                post.getContent(),
                post.getCreatedOn(),
                post.getUps(),
                post.getDowns(),
                post.getNumberOfComments()
        );
    }

    public PostDto createPost(int topicId, CreatePostDto data) {
        long userId = authService.getAuthenticatedUserId();
        Permission permission = topicMembershipRepository.findPermissionById(userId, topicId)
                .orElseThrow(() -> new NotAllowedException("You are not a member of the topic"));
        if (permission.val() < Permission.WRITE.val()) {
            throw new NotAllowedException("You don't have the permission to write to the topic");
        }
        Post post = new Post();
        post.setAuthor(new User(userId));
        post.setTopic(new Topic(topicId));
        post.setContent(data.content());
        post.setCreatedOn(LocalDateTime.now());
        post = postRepository.save(post);
        return postToGetPostRequest(postRepository.findById(post.getId()).get());
    }

    public List<UserIdHandle> getTopicMembers(int id) {
        long userId = authService.getAuthenticatedUserId();
        if (!isMemberOfTopic(userId, id)) {
            throw new NotAllowedException("Only a member of a topic can see list of its members");
        }
        return this.topicMembershipRepository.findMembersOf(id).stream()
                .map(result -> new UserIdHandle((long) result[0], (String) result[1]))
                .toList();
    }

    public void adminDeleteTopic(int id) {
        this.topicRepository.deleteById(id);
    }
}
