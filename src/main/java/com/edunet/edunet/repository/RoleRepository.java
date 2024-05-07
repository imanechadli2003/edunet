package com.edunet.edunet.repository;

import com.edunet.edunet.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role getRoleByName(String name);

    default Role getDefaultRole() {
        return getRoleByName("USER_ROLE");
    }
}
