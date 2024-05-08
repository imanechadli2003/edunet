package com.edunet.edunet.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;

    private int ups = 0;

    private int downs = 0;

    @OneToMany(mappedBy="post", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @Column(name = "number_of_comments")
    private int numberOfComments = 0;

    @Column(name = "created_on")
    private LocalDateTime createdOn;
}
