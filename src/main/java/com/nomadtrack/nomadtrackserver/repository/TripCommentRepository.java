package com.nomadtrack.nomadtrackserver.repository;

import com.nomadtrack.nomadtrackserver.model.TripComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripCommentRepository extends JpaRepository<TripComment, Integer> {
    List<TripComment> findAllByTrip_IdOrderByCreatedAtAsc(Integer tripId);
}
