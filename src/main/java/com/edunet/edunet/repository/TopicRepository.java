package com.edunet.edunet.repository;


import com.edunet.edunet.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {

    @Query("SELECT t.owner.id FROM Topic t WHERE t.id = :id")
    Optional<Integer> findIdOfOwner(int id);

    @Query("SELECT t.owner.handle FROM Topic t WHERE t.id = :id")
    Optional<String> findHandleOfOwner(int id);

    Optional<Topic> findTopicByName(String name);

    @Query("SELECT t.name FROM Topic t WHERE t.id = :id")
    Optional<String> findNameById(int id);

    @Query("SELECT t.privacy FROM Topic t WHERE t.name = :name")
    Optional<Topic.Privacy> getPrivacyByName(String name);

    @Query("SELECT t.id FROM Topic t WHERE t.name = :name")
    Optional<Integer> findIdByName(String name);
}
