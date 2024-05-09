package com.edunet.edunet.service;

import com.edunet.edunet.dto.PostUserRequest;
import com.edunet.edunet.dto.GetUserRequest;
import com.edunet.edunet.dto.UpdatePasswordRequest;
import com.edunet.edunet.model.Branch;
import com.edunet.edunet.model.User;
import com.edunet.edunet.repository.RoleRepository;
import com.edunet.edunet.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final TopicService topicService;

    private final BranchService branchService;

    public Optional<GetUserRequest> findUserById(int id) {
        Optional<User> user = userRepository.findUserById(id);
        return user.map(UserService::userToGetUserRequest);
    }

    public void save(PostUserRequest data) {
        User user = UserService.postUserRequestToUser(data);
        if (userRepository.existsByHandle(user.getHandle())) {
            throw new IllegalArgumentException("Handle already exist");
        }
        Optional<Branch> branch = branchService.getBranch(data.branch());
        user.setBranch(branch.orElseThrow(() -> new IllegalArgumentException("Unknown branch name" + data.branch())));
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setRole(roleRepository.getDefaultRole());
        user.setCreatedOn(LocalDate.now());
        userRepository.save(user);
        // TODO - Verify email
        // TODO - Create User topic and set permissions
    }

    public List<GetUserRequest> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserService::userToGetUserRequest)
                .toList();
    }

    public void updateUser(int id, PostUserRequest data) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        User user = postUserRequestToUser(data);
        if (userRepository.existsByHandle(user.getHandle())) {
            throw new IllegalArgumentException("Handle already exist");
        }
        Optional<Branch> branch = branchService.getBranch(data.branch());
        user.setBranch(branch.orElseThrow(() -> new IllegalArgumentException("Unknown branch name" + data.branch())));
        user.setId(id);
        userRepository.save(user);
    }

    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }

    public void updatePassword(int id, UpdatePasswordRequest password) {
        String oldPassword = userRepository.findPasswordById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String oldPasswordProvided = passwordEncoder.encode(password.oldPassword());
        if (!oldPasswordProvided.equals(oldPassword)) {
            throw new IllegalArgumentException("Incorrect password");
        }
        // TODO - Validate the new password
        if (!password.newPassword().equals(password.confirmPassword())) {
            throw new IllegalArgumentException("Password doesn't match");
        }
        String encodedNewPassword = passwordEncoder.encode(password.newPassword());
        userRepository.updatePassword(id, encodedNewPassword);
    }

    /**
     * Create a User instance and map trivial values from PostUserRequest.
     */
    private static User postUserRequestToUser(PostUserRequest data) {
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

    private static GetUserRequest userToGetUserRequest(User u) {
        return new GetUserRequest(
                u.getFirstName(),
                u.getLastName(),
                u.getHandle(),
                u.getEmail(),
                u.getCountry(),
                (u.getGender() != null)? u.getGender().toString(): "",
                (u.getGender() != null)? u.getBranch().getName(): "",
                u.getTitle(),
                u.getCreatedOn()
        );
    }
}
