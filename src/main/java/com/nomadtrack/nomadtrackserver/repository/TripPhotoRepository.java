package com.nomadtrack.nomadtrackserver.repository;

import com.nomadtrack.nomadtrackserver.model.TripPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripPhotoRepository extends JpaRepository<TripPhoto, Integer> {
    List<TripPhoto> findAllByTrip_IdOrderByCreatedAtAsc(Integer tripId);
}
