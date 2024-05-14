package com.edunet.edunet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String handle;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private Gender gender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    // TODO - Add promo info and student status

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String country = "French";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Set<Topic> ownedTopics;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<TopicMembership> memberships;

    @Column(name = "created_on")
    private LocalDate createdOn;

    public User(long id) {
        this.id = id;
    }

    public enum Gender {
        FEMALE(0), MALE(1);

        private final int val;

        Gender(int val) {
            this.val = val;
        }

        public int val() {
            return val;
        }

        public static Gender fromInt(int val) {
            for (Gender gender: Gender.values()) {
                if (gender.val() == val) {
                    return gender;
                }
            }
            throw new IllegalArgumentException("Gender must be either 0 for female or 1 for male");
        }
    }
}
