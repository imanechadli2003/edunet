package com.edunet.edunet.repository;

import com.edunet.edunet.model.TopicMembershipRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipRequestRepository extends JpaRepository<TopicMembershipRequest, Integer> {

    @Query("SELECT tmr.user.handle FROM TopicMembershipRequest tmr WHERE tmr.topic.id = :topicId")
    List<String> findHandlesByTopicId(int topicId);

    @Query("SELECT tmr.id, tmr.user.id, tmr.user.handle FROM TopicMembershipRequest tmr WHERE tmr.topic.id = :topicId")
    List<Object[]> findRequestUser(int topicId);
}
