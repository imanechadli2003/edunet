package com.edunet.edunet.repository;

import com.edunet.edunet.model.TopicMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import static com.edunet.edunet.model.TopicMembership.*;

import java.util.Optional;


@Repository
public interface MembershipRepository extends JpaRepository<TopicMembership, Long> {

    @Query("SELECT m.permission FROM TopicMembership m WHERE m.user.id = :uid AND m.topic.id = :tid")
    Optional<Permission> findPermissionById(long uid, int tid);
}
