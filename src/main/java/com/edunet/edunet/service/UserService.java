package com.edunet.edunet.service;

import com.edunet.edunet.dto.*;
import com.edunet.edunet.exception.*;
import com.edunet.edunet.model.*;
import com.edunet.edunet.repository.*;
import com.edunet.edunet.security.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final AuthenticationService authService;

    private final BranchRepository branchRepository;

    private final TopicRepository topicRepository;

    private MembershipRepository membershipRepository;

    private final PostRepository postRepository;

    private final PostService postService;

    private final Clock clock;

    public UserDto getUserById(Long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user " + id));
        return userToGetUserRequest(user);
    }

    public UserDto save(CreateUserDto data) {
        LocalDate now = LocalDate.now(clock);
        User user = UserService.postUserRequestToUser(data);
        if (userRepository.existsByHandle(user.getHandle())) {
            throw new HandleAlreadyExistsException(data.handle());
        }
        Branch branch = branchRepository.findBranchByName(data.branch())
                .orElseThrow(() -> new ResourceNotFoundException("branch " + data.branch()));
        user.setBranch(branch);
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setRole(roleRepository.getDefaultRole());
        user.setCreatedOn(now);
        User savedUser = userRepository.save(user);

        // TODO - Verify email

        Topic privateTopic = new Topic();
        privateTopic.setName("pr" + user.getId());
        privateTopic.setOwner(user);
        privateTopic.setPrivacy(Topic.Privacy.PRIVATE);
        privateTopic.setCreatedOn(now);
        privateTopic.setType(Topic.TopicType.USER_TOPIC);
        Topic savedTopic = topicRepository.save(privateTopic);
        TopicMembership ownership = new TopicMembership();
        ownership.setPermission(TopicMembership.Permission.OWNER);
        ownership.setUser(savedUser);
        ownership.setTopic(savedTopic);
        membershipRepository.save(ownership);

        Topic publicTopic = new Topic();
        publicTopic.setName("pu" + user.getId());
        publicTopic.setOwner(user);
        publicTopic.setPrivacy(Topic.Privacy.PUBLIC);
        publicTopic.setCreatedOn(now);
        publicTopic.setType(Topic.TopicType.USER_TOPIC);
        savedTopic = topicRepository.save(publicTopic);
        TopicMembership giveMeABreak = new TopicMembership();
        giveMeABreak.setPermission(TopicMembership.Permission.OWNER);
        giveMeABreak.setUser(savedUser);
        giveMeABreak.setTopic(savedTopic);
        membershipRepository.save(giveMeABreak);
        return userToGetUserRequest(savedUser);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserService::userToGetUserRequest)
                .toList();
    }

    public void updateUser(Long id, CreateUserDto data) {
        checkIfUserAuthenticated(id);
        User user = postUserRequestToUser(data);
        if (userRepository.existsByHandle(user.getHandle())) {
            throw new HandleAlreadyExistsException(data.handle());
        }
        Branch branch = branchRepository.findBranchByName(data.branch())
                .orElseThrow(() -> new ResourceNotFoundException("branch " + data.branch()));
        user.setBranch(branch);
        user.setId(id);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        checkIfUserAuthenticated(id);
        userRepository.deleteById(id);
    }

    public void updatePassword(Long id, UpdatePasswordDto password) {
        checkIfUserAuthenticated(id);
        String oldPassword = userRepository.findPasswordById(id)
                .orElseThrow(() -> new ApplicationError("unexpected error"));
        if (!passwordEncoder.matches(password.oldPassword(), oldPassword)) {
            throw new BadRequestException("Incorrect password");
        }
        // TODO - Validate the new password
        if (!password.newPassword().equals(password.confirmPassword())) {
            throw new BadRequestException("Password doesn't match");
        }
        String encodedNewPassword = passwordEncoder.encode(password.newPassword());
        userRepository.updatePassword(id, encodedNewPassword);
    }

    private void checkIfUserAuthenticated(Long id) {
        if (id != authService.getAuthenticatedUserId()) {
            throw new NotAllowedException("Not allowed to delete this resource: user " + id);
        }
    }


    /**
     * Create a User instance and map trivial values from CreateUserDto.
     */
    private static User postUserRequestToUser(CreateUserDto data) {
        User user = new User();
        user.setFirstName(data.firstName());
        user.setLastName(data.lastName());
        user.setEmail(data.email());
        user.setGender(User.Gender.fromInt(data.gender()));
        user.setCountry(data.country());
        user.setHandle(data.handle().toLowerCase());
        user.setTitle(data.title());
        return user;
    }

    private static UserDto userToGetUserRequest(User u) {
        return new UserDto(
                u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getHandle(),
                u.getEmail(),
                u.getCountry(),
                u.getGender().toString(),
                (u.getBranch() != null)? u.getBranch().getName(): null,
                u.getTitle(),
                u.getCreatedOn()
        );
    }

    public AuthToken getAuthenticatedUser() {
        long id = authService.getAuthenticatedUserId();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String handle = auth.getName();
        String role = auth.getAuthorities().stream().toList().get(0).getAuthority();
        return new AuthToken(id, handle, role, null);
    }

    public List<UserDto> search(String like, int page, int size) {
        PageRequest pr = PageRequest.of(page, size);
        return userRepository.findByHandleContaining(like, pr).stream()
                .map(UserService::userToGetUserRequest)
                .toList();
    }

    public List<PostDto> getUserPublicPosts(long id) {
        String handle = userRepository.findHandleById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user " + id));
        return this.postRepository.findPostByName("pu" + id).stream()
                .map(p -> new PostDto(
                        p.getId(),
                        "public posts",
                        handle,
                        p.getContent(),
                        p.getCreatedOn(),
                        p.getUps(),
                        p.getDowns(),
                        p.getNumberOfComments()
                )).toList();
    }

    public List<PostDto> getUserPrivatePosts(long id) {
        checkIfUserAuthenticated(id);
        String handle = userRepository.findHandleById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user " + id));
        return this.postRepository.findPostByName("pr" + id).stream()
                .map(p -> new PostDto(
                        p.getId(),
                        "public posts",
                        handle,
                        p.getContent(),
                        p.getCreatedOn(),
                        p.getUps(),
                        p.getDowns(),
                        p.getNumberOfComments()
                )).toList();
    }

    public PostDto createUserPublicPost(CreatePostDto data) {
        long id = authService.getAuthenticatedUserId();
        return postService.createPostForTopic("pu" + id, data);
    }

    public void adminDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }
}
