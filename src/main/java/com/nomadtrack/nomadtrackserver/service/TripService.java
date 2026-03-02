package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.dto.MapPinDto;
import com.nomadtrack.nomadtrackserver.model.dto.TripRequestDto;
import com.nomadtrack.nomadtrackserver.model.dto.TripResponseDto;
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
    public TripResponseDto create(
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

        tripRepository.save(trip);

        TripResponseDto tripResponseDto = new TripResponseDto();
        tripResponseDto.setId(trip.getId());
        tripResponseDto.setUserId(user.getId());
        tripResponseDto.setTitle(title);
        tripResponseDto.setCity(city);
        tripResponseDto.setCountry(country);
        tripResponseDto.setStartDate(startDate);
        tripResponseDto.setEndDate(endDate);
        tripResponseDto.setNotes(notes);
        tripResponseDto.setLatitude(latitude);
        tripResponseDto.setLongitude(longitude);
        tripResponseDto.setVisibility(visibility);


        return tripResponseDto;
    }

    // getAllUserTrips
    @Transactional(readOnly = true)
    public List<TripResponseDto> getAllUserTrips(Integer userId) {
        List<Trip> trips = tripRepository.findAll();
        List<TripResponseDto> tripResponseDtos = new ArrayList<>();
        for (Trip trip : trips) {
            if(trip.getUser().getId().equals(userId)) {
                TripResponseDto tripResponseDto = new TripResponseDto();
                tripResponseDto.setId(trip.getId());
                tripResponseDto.setUserId(trip.getUser().getId());
                tripResponseDto.setTitle(trip.getTitle());
                tripResponseDto.setCity(trip.getCity());
                tripResponseDto.setCountry(trip.getCountry());
                tripResponseDto.setStartDate(trip.getStartDate());
                tripResponseDto.setEndDate(trip.getEndDate());
                tripResponseDto.setNotes(trip.getNotes());
                tripResponseDto.setLatitude(trip.getLatitude());
                tripResponseDto.setLongitude(trip.getLongitude());
                tripResponseDtos.add(tripResponseDto);
            }

        }
        return tripResponseDtos;
    }
    // getAllUserTrips
    @Transactional(readOnly = true)
    public List<TripResponseDto> getAll() {
        List<Trip> trips = tripRepository.findAll();
        List<TripResponseDto> tripResponseDtos = new ArrayList<>();
        for (Trip trip : trips) {
            TripResponseDto tripResponseDto = new TripResponseDto();
            tripResponseDto.setId(trip.getId());
            tripResponseDto.setUserId(trip.getUser().getId());
            tripResponseDto.setTitle(trip.getTitle());
            tripResponseDto.setCity(trip.getCity());
            tripResponseDto.setCountry(trip.getCountry());
            tripResponseDto.setStartDate(trip.getStartDate());
            tripResponseDto.setEndDate(trip.getEndDate());
            tripResponseDto.setNotes(trip.getNotes());
            tripResponseDto.setLatitude(trip.getLatitude());
            tripResponseDto.setLongitude(trip.getLongitude());
            tripResponseDtos.add(tripResponseDto);
        }
        return tripResponseDtos;
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
    public TripResponseDto update(Integer tripId, Integer userId, TripRequestDto updated) {
        Trip existing = getById(tripId);

        if(!userId.equals(existing.getUser().getId())) {
            throw new IllegalArgumentException("Cannot update trip that isn't yours: userId=" + userId + " tripOwnerId=" + existing.getUser().getId());
        }

        existing.setUser(existing.getUser());
        existing.setTitle(updated.getTitle());
        existing.setCity(updated.getCity());
        existing.setCountry(updated.getCountry());
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());
        existing.setNotes(updated.getNotes());
        existing.setLatitude(updated.getLatitude());
        existing.setLongitude(updated.getLongitude());
        existing.setVisibility(updated.getVisibility());

        tripRepository.save(existing);

        TripResponseDto tripResponseDto = new TripResponseDto();
        tripResponseDto.setId(tripId);
        tripResponseDto.setUserId(userId);
        tripResponseDto.setTitle(updated.getTitle());
        tripResponseDto.setCity(updated.getCity());
        tripResponseDto.setCountry(updated.getCountry());
        tripResponseDto.setStartDate(updated.getStartDate());
        tripResponseDto.setEndDate(updated.getEndDate());
        tripResponseDto.setNotes(updated.getNotes());
        tripResponseDto.setLatitude(updated.getLatitude());
        tripResponseDto.setLongitude(updated.getLongitude());
        tripResponseDto.setVisibility(updated.getVisibility());


        return tripResponseDto;
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
