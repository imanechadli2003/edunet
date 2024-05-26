package com.edunet.edunet.repository;

import com.edunet.edunet.model.Topic;
import com.edunet.edunet.model.TopicMembership;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import static com.edunet.edunet.model.TopicMembership.*;

import java.util.List;
import java.util.Optional;


@Repository
public interface MembershipRepository extends JpaRepository<TopicMembership, Long> {

    @Query("SELECT m.permission FROM TopicMembership m WHERE m.user.id = :uid AND m.topic.id = :tid")
    Optional<Permission> findPermissionById(long uid, int tid);

    @Query("SELECT m.topic FROM TopicMembership m WHERE m.user.id = :id")
    List<Topic> findTopicsForUserById(long id, PageRequest pr);
}
