package com.edunet.edunet.repository;

import com.edunet.edunet.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {


    @Query("SELECT p FROM Post p WHERE p.topic.name = :topic")
    Page<Post> findAllByTopic(String topic, PageRequest pr);

    @Query("DELETE FROM Post p WHERE p.id = :postId AND p.author.id = :userId")
    void deletePostIfAuthorIdMatch(Integer postId, Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.ups = p.ups + 1 WHERE p.id = :id")
    void incrementUps(int id);

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.downs = p.downs + 1 WHERE p.id = :id")
    void incrementDowns(int id);
}
