package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.dto.MapPinDto;
import com.nomadtrack.nomadtrackserver.model.dto.TripRequestDto;
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
    public TripRequestDto create(
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

        TripRequestDto tripRequestDto = new TripRequestDto();
        tripRequestDto.setTitle(title);
        tripRequestDto.setCity(city);
        tripRequestDto.setCountry(country);
        tripRequestDto.setStartDate(startDate);
        tripRequestDto.setEndDate(endDate);
        tripRequestDto.setNotes(notes);
        tripRequestDto.setLatitude(latitude);
        tripRequestDto.setLongitude(longitude);
        tripRequestDto.setVisibility(visibility);

        tripRepository.save(trip);

        return tripRequestDto;
    }

    // getAllUserTrips
    @Transactional(readOnly = true)
    public List<TripRequestDto> getAll() {
        List<Trip> trips = tripRepository.findAll();
        List<TripRequestDto> tripRequestDtos = new ArrayList<>();
        for (Trip trip : trips) {
            TripRequestDto tripRequestDto = new TripRequestDto();
            tripRequestDto.setTitle(trip.getTitle());
            tripRequestDto.setCity(trip.getCity());
            tripRequestDto.setCountry(trip.getCountry());
            tripRequestDto.setStartDate(trip.getStartDate());
            tripRequestDto.setEndDate(trip.getEndDate());
            tripRequestDto.setNotes(trip.getNotes());
            tripRequestDto.setLatitude(trip.getLatitude());
            tripRequestDto.setLongitude(trip.getLongitude());
            tripRequestDtos.add(tripRequestDto);
        }
        return tripRequestDtos;
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

    // getByCountryName
    @Transactional(readOnly = true)
    public List<TripRequestDto> getByCountryName(String countryName) {
        if (countryName == null) {
            throw new IllegalArgumentException("countryName is required");
        }
        List<Trip> trips = tripRepository.findByCountryIgnoreCase(countryName);

        List<TripRequestDto> tripRequestDtos = new ArrayList<>();
        for (Trip trip : trips) {
            TripRequestDto tripRequestDto = new TripRequestDto();
            tripRequestDto.setTitle(trip.getTitle());
            tripRequestDto.setCity(trip.getCity());
            tripRequestDto.setCountry(trip.getCountry());
            tripRequestDto.setStartDate(trip.getStartDate());
            tripRequestDto.setEndDate(trip.getEndDate());
            tripRequestDto.setNotes(trip.getNotes());
            tripRequestDto.setLatitude(trip.getLatitude());
            tripRequestDto.setLongitude(trip.getLongitude());
            tripRequestDto.setVisibility(trip.getVisibility());
            tripRequestDtos.add(tripRequestDto);
        }

        return tripRequestDtos;
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
    public TripRequestDto update(Integer tripId, TripRequestDto updated) {
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

        TripRequestDto tripRequestDto = new TripRequestDto();
        tripRequestDto.setTitle(updated.getTitle());
        tripRequestDto.setCity(updated.getCity());
        tripRequestDto.setCountry(updated.getCountry());
        tripRequestDto.setStartDate(updated.getStartDate());
        tripRequestDto.setEndDate(updated.getEndDate());
        tripRequestDto.setNotes(updated.getNotes());
        tripRequestDto.setLatitude(updated.getLatitude());
        tripRequestDto.setLongitude(updated.getLongitude());
        tripRequestDto.setVisibility(updated.getVisibility());
        tripRepository.save(existing);

        return tripRequestDto;
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
