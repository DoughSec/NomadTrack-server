package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.TripPhoto;
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

    // getAll
    @Transactional(readOnly = true)
    public List<TripPhoto> getAll(Integer tripId) {
        return tripPhotoRepository.findAllByTrip_IdOrderByCreatedAtAsc(tripId);
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
}
