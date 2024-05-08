package com.edunet.edunet.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "topic_user")
public class TopicMembership {

    @Id
    @Embedded
    private TopicUserId id;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private Permission permission = Permission.WRITE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    public record TopicUserId(int tId, int uId) {}

    public enum Permission {
        READ, COMMENT, WRITE, OWNER
    }
}
