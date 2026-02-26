package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.TripPhoto;
import com.nomadtrack.nomadtrackserver.model.dto.TripPhotoDto;
import com.nomadtrack.nomadtrackserver.model.dto.TripPhotoResponseDto;
import com.nomadtrack.nomadtrackserver.repository.TripRepository;
import com.nomadtrack.nomadtrackserver.repository.TripPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TripPhotoService {

    private final TripPhotoRepository tripPhotoRepository;
    private final TripRepository tripRepository;

    public TripPhotoService(
            TripPhotoRepository tripPhotoRepository,
            TripRepository tripRepository
    ) {
        this.tripPhotoRepository = tripPhotoRepository;
        this.tripRepository = tripRepository;
    }

    // create TripPhoto
    public TripPhoto create(
            Integer tripId, String url, String caption, Integer sortOrder
    ) {
        if (tripId == null) {
            throw new IllegalArgumentException("tripId is required");
        }

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));

        TripPhoto tripPhoto = new TripPhoto();
        tripPhoto.setTrip(trip);
        tripPhoto.setUrl(url);
        tripPhoto.setCaption(caption);
        tripPhoto.setSortOrder(sortOrder);

        return tripPhotoRepository.save(tripPhoto);
    }

    // createForUser - verifies trip belongs to the current user before creating
    public TripPhotoResponseDto createForUser(
            Integer tripId, Integer userId, String url, String caption, Integer sortOrder
    ) {
        if (tripId == null) {
            throw new IllegalArgumentException("tripId is required");
        }

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));

        if (!trip.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Trip does not belong to the current user: " + tripId);
        }

        TripPhoto tripPhoto = new TripPhoto();
        tripPhoto.setTrip(trip);
        tripPhoto.setUrl(url);
        tripPhoto.setCaption(caption);
        tripPhoto.setSortOrder(sortOrder);

        TripPhoto saved = tripPhotoRepository.save(tripPhoto);

        TripPhotoResponseDto dto = new TripPhotoResponseDto();
        dto.setPhotoId(saved.getId());
        dto.setTripId(tripId);
        dto.setUrl(url);
        dto.setCaption(caption);
        dto.setSortOrder(sortOrder);


        return dto;
    }

    // getAll
    @Transactional(readOnly = true)
    public List<TripPhoto> getAll(Integer tripId) {
        return tripPhotoRepository.findAllByTrip_IdOrderByCreatedAtAsc(tripId);
    }

    // getAllByUserId - returns only photos for trips owned by the given user
    @Transactional(readOnly = true)
    public List<TripPhotoResponseDto> getAllByUserId(Integer tripId, Integer userId) {
        List<Trip> userTrips = tripRepository.findByUser_Id(userId);
        boolean tripBelongsToUser = userTrips.stream()
                .anyMatch(trip -> trip.getId().equals(tripId));
        if (!tripBelongsToUser) {
            throw new IllegalArgumentException("Trip not found for current user: " + tripId);
        }
        return tripPhotoRepository.findAllByTrip_IdOrderByCreatedAtAsc(tripId)
                .stream()
                .map(photo -> {
                    TripPhotoResponseDto dto = new TripPhotoResponseDto();
                    dto.setPhotoId(photo.getId());
                    dto.setTripId(tripId);
                    dto.setUrl(photo.getUrl());
                    dto.setCaption(photo.getCaption());
                    dto.setSortOrder(photo.getSortOrder());
                    return dto;
                })
                .toList();
    }

    // delete TripPhoto
    public void delete(Integer tripPhotoId) {
        if (tripPhotoId == null) {
            throw new IllegalArgumentException("TripPhotoId is required");
        }
        if (!tripPhotoRepository.existsById(tripPhotoId)) {
            throw new IllegalArgumentException("TripPhoto not found: " + tripPhotoId);
        }
        tripPhotoRepository.deleteById(tripPhotoId);
    }

    // deleteForUser - verifies photo's trip belongs to the current user before deleting
    public void deleteForUser(Integer tripPhotoId, Integer userId) {
        if (tripPhotoId == null) {
            throw new IllegalArgumentException("TripPhotoId is required");
        }

        TripPhoto tripPhoto = tripPhotoRepository.findById(tripPhotoId)
                .orElseThrow(() -> new IllegalArgumentException("TripPhoto not found: " + tripPhotoId));

        if (!tripPhoto.getTrip().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("TripPhoto does not belong to the current user: " + tripPhotoId);
        }

        tripPhotoRepository.deleteById(tripPhotoId);
    }
}
