package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.dto.MapPinDto;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public TripService(
            TripRepository tripRepository,
            UserRepository userRepository
    ) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    // create Trip
    public Trip create(
            Integer userId, String title, String city, String country, LocalDate startDate, LocalDate endDate,
            String notes, BigDecimal latitude, BigDecimal longitude, String visibility
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setTitle(title);
        trip.setCity(city);
        trip.setCountry(country);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setNotes(notes);
        trip.setLatitude(latitude);
        trip.setLongitude(longitude);
        trip.setVisibility(visibility);

        return tripRepository.save(trip);
    }

    // getAll
    @Transactional(readOnly = true)
    public List<Trip> getAll() {
        return tripRepository.findAll();
    }

    // getById
    @Transactional(readOnly = true)
    public Trip getById(Integer tripId) {
        if (tripId == null) {
            throw new IllegalArgumentException("TripId is required");
        }
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));
    }

    public List<MapPinDto> getMapPins() {
        List<Trip> trips = tripRepository.findAll();
        List<MapPinDto> pins = new ArrayList<>();

        for (Trip trip : trips) {
            if (trip.getLatitude() != null && trip.getLongitude() != null) {
                pins.add(new MapPinDto(
                        trip.getId(),
                        trip.getCity(),
                        trip.getCountry(),
                        trip.getLatitude(),
                        trip.getLongitude()
                ));
            }
        }
        return pins;
    }

    // update Trip
    public Trip update(Integer tripId, Trip updated) {
        Trip existing = getById(tripId);

        existing.setTitle(updated.getTitle());
        existing.setCity(updated.getCity());
        existing.setCountry(updated.getCountry());
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());
        existing.setNotes(updated.getNotes());
        existing.setLatitude(updated.getLatitude());
        existing.setLongitude(updated.getLongitude());
        existing.setVisibility(updated.getVisibility());

        return tripRepository.save(existing);
    }

    // delete Trip
    public void delete(Integer tripId) {
        if (tripId == null) {
            throw new IllegalArgumentException("TripId is required");
        }
        if (!tripRepository.existsById(tripId)) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
        }
        tripRepository.deleteById(tripId);
    }
}
