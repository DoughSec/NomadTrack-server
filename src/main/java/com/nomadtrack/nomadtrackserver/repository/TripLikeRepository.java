package com.nomadtrack.nomadtrackserver.repository;

import com.nomadtrack.nomadtrackserver.model.TripLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripLikeRepository extends JpaRepository<TripLike, Integer> {
    List<TripLike> findAllByTrip_IdOrderByCreatedAtAsc(Integer tripId);
    Optional<TripLike> findByIdAndTrip_Id(Integer id, Integer tripId);
}
