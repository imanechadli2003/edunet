package com.edunet.edunet.repository;

import com.edunet.edunet.model.Role;
import com.edunet.edunet.model.User;
import com.edunet.edunet.repository.projections.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserById(Long id);

    boolean existsByHandle(String handle);

    Optional<User> findUserByHandle(String handle);

    @Query("SELECT u.password FROM User u WHERE u.id = :id")
    Optional<String> findPasswordById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :id")
    void updatePassword(Long id, String newPassword);


    @Query("SELECT u.id FROM User u WHERE u.handle = :handle")
    Optional<Long> findIdByHandle(String handle);

    Optional<Credentials> findByHandle(String handle);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.role = :role WHERE u.id = :id")
    void updateRoleById(long id, Role role);
}
