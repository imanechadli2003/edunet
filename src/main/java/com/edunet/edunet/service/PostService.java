package com.edunet.edunet.service;


import com.edunet.edunet.dto.GetPostRequest;
import com.edunet.edunet.dto.PostPostRequest;
import com.edunet.edunet.dto.UpdatePostRequest;
import com.edunet.edunet.dto.Vote;
import com.edunet.edunet.model.Post;
import com.edunet.edunet.model.Topic;
import com.edunet.edunet.model.User;
import com.edunet.edunet.repository.PostRepository;
import com.edunet.edunet.repository.MembershipRepository;
import com.edunet.edunet.repository.TopicRepository;
import com.edunet.edunet.security.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static com.edunet.edunet.model.TopicMembership.*;
import static com.edunet.edunet.model.Topic.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final TopicRepository topicRepository;

    private final MembershipRepository membershipRepository;

    private AuthenticationService authService;

    public GetPostRequest createNewPost(PostPostRequest data) {
        long userId = authService.getAuthenticatedUserId();
        int topicId = (data.topicId() != null)? data.topicId(): topicRepository.findIdByName(data.topicName())
                        .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
        Permission permission = membershipRepository.findPermissionById(userId, topicId)
                    .orElseThrow(() -> new IllegalArgumentException("Unauthorized action"));
        if (permission.val() < Permission.WRITE.val()) {
            throw new IllegalArgumentException("Unauthorized: You do not have permission to perform this action");
        }
        Post post = new Post();
        post.setAuthor(new User(userId));
        post.setTopic(new Topic(topicId));
        post.setContent(data.content());
        post.setCreatedOn(LocalDateTime.now());
        post = postRepository.save(post);
        return postToGetPostRequest(postRepository.findById(post.getId()).get());
    }

    public List<GetPostRequest> getPosts(String topic, int page, int size) {
        Privacy privacy = topicRepository.getPrivacyByName(topic)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
        if (privacy == Privacy.PRIVATE) {
            long userId = authService.getAuthenticatedUserId();
            Optional<Integer> topicId = topicRepository.findIdByName(topic);
            if (topicId.isEmpty() || !isMemberOfTopic(userId, topicId.get())) {
                throw new IllegalArgumentException("Not Authorized");
            }
        }
        PageRequest pr = PageRequest.of(page, size);
        return postRepository.findAllByTopic(topic, pr).stream()
                .map(PostService::postToGetPostRequest)
                .toList();
    }

    public GetPostRequest updatePost(int id, UpdatePostRequest data) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setContent(data.content());
        postRepository.save(post);
        return postToGetPostRequest(post);
    }

    public void deletePost(int postId) {
        // TODO - The owner of a topic must be able to delete posts too
        Long userId = authService.getAuthenticatedUserId();
        postRepository.deletePostIfAuthorIdMatch(postId, userId);
    }

    private boolean isMemberOfTopic(long userId, int topicId) {
        return membershipRepository.findPermissionById(userId, topicId).isPresent();
    }

    private static GetPostRequest postToGetPostRequest(Post post) {
        return new GetPostRequest(
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
        if (vote.v() == 1) {
            postRepository.incrementUps(id);
        } else {
            postRepository.incrementDowns(id);
        }
    }
}
