package com.edunet.edunet.repository;

import com.edunet.edunet.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :id")
    List<Comment> findByPost(int id);

    Optional<Comment> findById(int id);
}
