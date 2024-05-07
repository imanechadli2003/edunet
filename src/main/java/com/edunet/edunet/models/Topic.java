package com.edunet.edunet.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    private String description;

    @Enumerated
    @Column(columnDefinition = "smallint", nullable = false)
    private Privacy privacy = Privacy.PRIVATE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated
    @Column(columnDefinition = "smallint", nullable = false)
    private TopicType type = TopicType.CREATED_TOPIC;

    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY)
    private List<Post> posts;

    @Column(name = "created_on", nullable = false)
    private LocalDate createdOn;

    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY)
    private List<TopicMembership> members;


    public enum Privacy {
        PUBLIC, PRIVATE
    }

    public enum TopicType {
        CREATED_TOPIC, USER_TOPIC
    }
}
