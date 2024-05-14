package com.edunet.edunet.service;


import com.edunet.edunet.dto.GetPostRequest;
import com.edunet.edunet.dto.PostPostRequest;
import com.edunet.edunet.model.Post;
import com.edunet.edunet.model.Topic;
import com.edunet.edunet.model.User;
import com.edunet.edunet.repository.PostRepository;
import com.edunet.edunet.repository.MembershipRepository;
import com.edunet.edunet.repository.TopicRepository;
import com.edunet.edunet.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import static com.edunet.edunet.model.TopicMembership.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    private final MembershipRepository membershipRepository;

    public GetPostRequest createNewPost(PostPostRequest data) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt principal = (Jwt) auth.getPrincipal();
        Long userId = principal.getClaim("userId");
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
        return postToGetPostRequest(post);
    }

    public List<GetPostRequest> getPosts(String topic, int page, int size) {
        // TODO - Reiterate
        Topic.Privacy privacy = topicRepository.getPrivacyByName(topic)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
        PageRequest pr = PageRequest.of(page, size);
        return postRepository.findAllByTopic(topic, pr).stream()
                .map(PostService::postToGetPostRequest)
                .toList();

    }

    private static GetPostRequest postToGetPostRequest(Post post) {
        // TODO - Reiterate
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
}
