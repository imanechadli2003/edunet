package com.edunet.edunet.service;

import com.edunet.edunet.dto.PostDto;
import com.edunet.edunet.dto.CreatePostDto;
import com.edunet.edunet.dto.Vote;
import com.edunet.edunet.exception.NotAllowedException;
import com.edunet.edunet.exception.ResourceNotFoundException;
import com.edunet.edunet.model.Post;
import com.edunet.edunet.model.Topic;
import com.edunet.edunet.model.TopicMembership;
import com.edunet.edunet.model.User;
import com.edunet.edunet.repository.PostRepository;
import com.edunet.edunet.repository.MembershipRepository;
import com.edunet.edunet.repository.TopicRepository;
import com.edunet.edunet.repository.UserRepository;
import com.edunet.edunet.security.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;

import static com.edunet.edunet.model.Topic.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    private final MembershipRepository membershipRepository;

    private AuthenticationService authService;


    public List<PostDto> getPosts(String topic, int page, int size) {
        Privacy privacy = topicRepository.getPrivacyByName(topic)
                .orElseThrow(() -> new ResourceNotFoundException("topic " + topic));
        if (privacy == Privacy.PRIVATE) {
            long userId = authService.getAuthenticatedUserId();
            Optional<Integer> topicId = topicRepository.findIdByName(topic);
            if (topicId.isEmpty() || !isMemberOfTopic(userId, topicId.get())) {
                throw new NotAllowedException("Not Authorized");
            }
        }
        PageRequest pr = PageRequest.of(page, size);
        return postRepository.findAllByTopic(topic, pr).stream()
                .map(PostService::postToGetPostRequest)
                .toList();
    }

    public PostDto updatePost(int id, CreatePostDto data) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setContent(data.content());
        postRepository.save(post);
        return postToGetPostRequest(post);
    }

    public void deletePost(int postId) {
        long userId = authService.getAuthenticatedUserId();
        try {
            checkIfAuthUserIsAuthor(postId, userId);
        } catch (NotAllowedException ignored) {
            checkIfAuthUserIsTopicOwner(postId, userId);
        }
        postRepository.deletePost(postId);
    }

    private void checkIfAuthUserIsTopicOwner(int postId, long userId) {
        long topicOwner = postRepository.findTopicOwnerId(postId)
                .orElseThrow(() -> new ResourceNotFoundException("post " + postId));
        if (topicOwner != userId) {
            throw new NotAllowedException("Not allowed");
        }
    }

    private void checkIfAuthUserIsAuthor(int postId, long userId) {
        long authorId = postRepository.findAuthorId(postId)
                .orElseThrow(() -> new ResourceNotFoundException("post " + postId));
        if (authorId != userId) {
            throw new NotAllowedException("Not allowed");
        }
    }

    private boolean isMemberOfTopic(long userId, int topicId) {
        return membershipRepository.findPermissionById(userId, topicId).isPresent();
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

    public void vote(int id, Vote vote) {
        // TODO - check permissions
        if (vote.v() == 1) {
            postRepository.incrementUps(id);
        } else {
            postRepository.incrementDowns(id);
        }
    }

    public List<PostDto> getUserPublicPosts(long id) {
        PageRequest pr = PageRequest.of(0, 10);
        return postRepository.findAllByTopic("pu" + id, pr)
                .map(PostService::postToGetPostRequest)
                .toList();
    }

    public List<PostDto> getUserPrivatePosts(long id) {
        if (authService.getAuthenticatedUserId() != id) {
            throw new NotAllowedException("These posts are private");
        }
        PageRequest pr = PageRequest.of(0, 10);
        return postRepository.findAllByTopic("pr" + id, pr)
                .map(PostService::postToGetPostRequest)
                .toList();
    }

    public PostDto createPostForTopic(String name, CreatePostDto data) {
        long userId = authService.getAuthenticatedUserId();
        int topicId = topicRepository.findIdByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("topic " + name));
        TopicMembership.Permission permission = membershipRepository.findPermissionById(userId, topicId)
                .orElseThrow(() -> new NotAllowedException("You are not a member of the topic"));
        if (permission.val() < TopicMembership.Permission.WRITE.val()) {
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
}
