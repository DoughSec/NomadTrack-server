package com.nomadtrack.nomadtrackserver.repository;

import com.nomadtrack.nomadtrackserver.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Integer> {
    List<Trip> findByVisibilityIgnoreCase(String visibility);
}
