package com.edunet.edunet.security;


import com.edunet.edunet.dto.RoleDto;
import com.edunet.edunet.model.Role;
import com.edunet.edunet.repository.RoleRepository;
import com.edunet.edunet.repository.TopicRepository;
import com.edunet.edunet.repository.UserRepository;
import com.edunet.edunet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ManagementService {

    private final UserRepository userRepository;

    private final TopicRepository topicRepository;

    private final RoleRepository roleRepository;

    public void deleteUserAccount(long id) {
        userRepository.deleteById(id);
    }

    public void deleteTopic(int id) {
        topicRepository.deleteById(id);
    }

    public void updateUserRole(long id, RoleDto newRole) {
        Role role = roleRepository.findByName(newRole.role())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        userRepository.updateRoleById(id, role);
    }


}
