package com.edunet.edunet.repository;

import com.edunet.edunet.dto.PostDto;
import com.edunet.edunet.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {


    @Query("SELECT p FROM Post p WHERE p.topic.name = :topic")
    Page<Post> findAllByTopic(String topic, PageRequest pr);

    @Transactional
    @Modifying
    @Query("DELETE FROM Post p WHERE p.id = :id")
    void deletePost(int id);

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.ups = p.ups + 1 WHERE p.id = :id")
    void incrementUps(int id);

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.downs = p.downs + 1 WHERE p.id = :id")
    void incrementDowns(int id);

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.numberOfComments = p.numberOfComments + 1 WHERE p.id = :id")
    void incrementComments(int id);

    @Query("SELECT p FROM Post p WHERE p.topic.id = :id")
    List<Post> findAllById(int id, PageRequest pr);

    @Query("SELECT p FROM Post p WHERE p.topic.name = :name")
    List<Post> findPostByName(String name);


    @Query("SELECT p.author.id FROM Post p WHERE p.id = :id")
    Optional<Long> findAuthorId(int id);

    @Query("SELECT p.topic.owner.id FROM Post p WHERE p.id = :id")
    Optional<Integer> findTopicOwnerId(int id);
}
