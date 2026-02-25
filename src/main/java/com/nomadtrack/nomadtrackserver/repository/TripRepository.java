package com.nomadtrack.nomadtrackserver.repository;

import com.nomadtrack.nomadtrackserver.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Integer> {
    List<Trip> findByVisibilityIgnoreCase(String visibility);
    List<Trip> findByCountryIgnoreCase(String country);
    List<Trip> findByUser_Id(Integer userId);
}
