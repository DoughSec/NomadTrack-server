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
        name = "trip_likes",
        indexes = {
                @Index(name = "idx_trip_likes_trip_id", columnList = "trip_id")
        }
)

public class TripLike {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "trip_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_trip_likes_trip")
    )
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}