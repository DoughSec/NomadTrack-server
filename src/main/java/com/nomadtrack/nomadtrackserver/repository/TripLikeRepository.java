package com.nomadtrack.nomadtrackserver.repository;

import com.nomadtrack.nomadtrackserver.model.TripComment;
import com.nomadtrack.nomadtrackserver.model.TripLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripLikeRepository extends JpaRepository<TripLike, Integer> {
    List<TripLike> findAllByTrip_IdOrderByCreatedAtAsc(Integer tripId);
}
