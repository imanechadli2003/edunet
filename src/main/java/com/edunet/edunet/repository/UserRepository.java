package com.edunet.edunet.repository;

import com.edunet.edunet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserById(Integer id);

    boolean existsByHandle(String handle);
}
