package com.nomadtrack.nomadtrackserver.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "followers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_follower_followee", columnNames = {"follower_id", "followee_id"})
        },
        indexes = {
                @Index(name = "idx_followers_follower_id", columnList = "follower_id"),
                @Index(name = "idx_followers_followee_id", columnList = "followee_id")
        }
)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // who is doing the following
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_followers_follower"))
    private User follower;

    // who is being followed
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "followee_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_followers_followee"))
    private User followee;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}