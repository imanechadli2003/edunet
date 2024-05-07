package com.edunet.edunet.service;

import com.edunet.edunet.dto.CreateUserRequest;
import com.edunet.edunet.models.Branch;
import com.edunet.edunet.models.Topic;
import com.edunet.edunet.models.User;
import com.edunet.edunet.repository.RoleRepository;
import com.edunet.edunet.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final TopicService topicService;

    private final BranchService branchService;

    public Optional<User> findUserById(int id) {
        return userRepository.findUserById(id);
    }

    public void save(CreateUserRequest data) {
        User user = new User();
        user.setFirstName(data.firstName());
        user.setLastName(data.lastName());
        user.setEmail(data.email());
        user.setGender(User.Gender.fromInt(data.gender()));
        user.setCountry(data.country());
        user.setHandle(data.firstName() + data.lastName());
        user.setTitle(data.title());
        Optional<Branch> branch = branchService.getBranch(data.branch());
        user.setBranch(branch.orElseThrow(() -> new IllegalArgumentException("Unknown branch" + data.branch())));
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setRole(roleRepository.getDefaultRole());
        user.setCreatedOn(LocalDate.now());
        userRepository.save(user);
        // TODO - Create User topic and set permissions
    }
}
