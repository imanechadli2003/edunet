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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private Permission permission = Permission.WRITE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    public record TopicUserId(int tId, long uId) {}

    public enum Permission {
        // can read posts and vote
        READ(0),
        // READ + can comment
        COMMENT(1),
        // COMMENT + can write posts
        WRITE(2),
        // WRITE + can update user memberships
        OWNER(3);

        private final int val;

        Permission(int val) {
            this.val = val;
        }

        public int val() {
            return val;
        }

        public static Permission fromInt(int val) {
            for (Permission p: Permission.values()) {
                if (val == p.val()) {
                    return p;
                }
            }
            throw new IllegalArgumentException("Not a valid value");
        }
    }
}
