package com.nomadtrack.nomadtrackserver.repository;

import com.nomadtrack.nomadtrackserver.model.TripComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripCommentRepository extends JpaRepository<TripComment, Integer> {
    List<TripComment> findAllByTrip_IdOrderByCreatedAtAsc(Integer tripId);
    List<TripComment> findAllByUser_Id(Integer userId);
}
