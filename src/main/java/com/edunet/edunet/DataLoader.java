package com.edunet.edunet;

import com.edunet.edunet.model.Branch;
import com.edunet.edunet.model.Role;
import com.edunet.edunet.model.Topic;
import com.edunet.edunet.model.User;
import com.edunet.edunet.repository.BranchRepository;
import com.edunet.edunet.repository.RoleRepository;
import com.edunet.edunet.repository.TopicRepository;
import com.edunet.edunet.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@AllArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final TopicRepository topicRepository;

    private final BranchRepository branchRepository;

    @Override
    public void run(String... args) {

        roleRepository.save(new Role(0, "admin", "Platform admin"));
        roleRepository.save(new Role(0, "manager", "Platform content and user management"));
        roleRepository.save(new Role(0, "user", "Students and visitors"));

        branchRepository.save(new Branch(0, "Software Engineering", ""));
        branchRepository.save(new Branch(0, "Network Engineering", ""));
        branchRepository.save(new Branch(0, "Data Engineering", ""));
        branchRepository.save(new Branch(0, "AI Engineering", ""));
        branchRepository.save(new Branch(0, "Embedded Systems Engineering", ""));

        User admin = new User();
        admin.setTitle("Admin");
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        Role role = roleRepository.getRoleByName("admin");
        admin.setRole(role);
        admin.setHandle("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(admin);

        Topic general = new Topic();
        general.setOwner(admin);
        general.setName("General");
        general.setCreatedOn(LocalDate.now());
        general.setPrivacy(Topic.Privacy.PUBLIC);
        topicRepository.save(general);
    }
}
