package com.edunet.edunet.repository;

import com.edunet.edunet.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role getRoleByName(String name);

    default Role getDefaultRole() {
        return getRoleByName("user");
    }

    Optional<Role> findByName(String name);
}
