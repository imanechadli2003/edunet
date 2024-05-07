package com.edunet.edunet;

import com.edunet.edunet.models.Branch;
import com.edunet.edunet.models.Role;
import com.edunet.edunet.models.Topic;
import com.edunet.edunet.models.User;
import com.edunet.edunet.repository.BranchRepository;
import com.edunet.edunet.repository.RoleRepository;
import com.edunet.edunet.repository.TopicRepository;
import com.edunet.edunet.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
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
    @DependsOn("dataSourceInitializer")
    public void run(String... args) throws Exception {

        roleRepository.save(new Role(0, "ROLE_ADMIN", "Platform admin"));
        roleRepository.save(new Role(0, "ROLE_MODERATOR", "Platform content and user management"));
        roleRepository.save(new Role(0, "ROLE_USER", "Students and visitors"));

        branchRepository.save(new Branch(0, "Software Engineering", ""));
        branchRepository.save(new Branch(0, "Network Engineering", ""));
        branchRepository.save(new Branch(0, "Data Engineering", ""));
        branchRepository.save(new Branch(0, "AI Engineering", ""));
        branchRepository.save(new Branch(0, "Embedded Systems Engineering", ""));

        User admin = new User();
        admin.setTitle("Admin");
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        Role role = roleRepository.getRoleByName("ROLE_ADMIN");
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
