package com.edunet.edunet.service;

import com.edunet.edunet.dto.PostUserRequest;
import com.edunet.edunet.dto.GetUserRequest;
import com.edunet.edunet.dto.UpdatePasswordRequest;
import com.edunet.edunet.exception.*;
import com.edunet.edunet.model.Branch;
import com.edunet.edunet.model.User;
import com.edunet.edunet.repository.RoleRepository;
import com.edunet.edunet.repository.UserRepository;
import com.edunet.edunet.security.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final AuthenticationService authService;

    private final BranchService branchService;

    public GetUserRequest findUserById(Long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user " + id));
        return userToGetUserRequest(user);
    }

    public void save(PostUserRequest data) {
        User user = UserService.postUserRequestToUser(data);
        if (userRepository.existsByHandle(user.getHandle())) {
            throw new HandleAlreadyExistsException(data.handle());
        }
        Branch branch = branchService.getBranch(data.branch())
                .orElseThrow(() -> new BadRequestException("Unknown branch " + data.branch()));
        user.setBranch(branch);
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

    public void updateUser(Long id, PostUserRequest data) {
        checkIfUserAuthenticated(id);
        User user = postUserRequestToUser(data);
        if (userRepository.existsByHandle(user.getHandle())) {
            throw new HandleAlreadyExistsException(data.handle());
        }
        Branch branch = branchService.getBranch(data.branch())
                .orElseThrow(() -> new BadRequestException("Unknown branch: " + data.branch()));
        user.setBranch(branch);
        user.setId(id);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        checkIfUserAuthenticated(id);
        userRepository.deleteById(id);
    }

    public void updatePassword(Long id, UpdatePasswordRequest password) {
        checkIfUserAuthenticated(id);
        String oldPassword = userRepository.findPasswordById(id)
                .orElseThrow(() -> new ApplicationError("unexpected error"));
        String oldPasswordProvided = passwordEncoder.encode(password.oldPassword());
        if (!oldPasswordProvided.equals(oldPassword)) {
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
