package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.TripLike;
import com.nomadtrack.nomadtrackserver.repository.TripRepository;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.repository.TripLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TripLikeService {

    private final TripLikeRepository tripLikeRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public TripLikeService(
            TripLikeRepository tripLikeRepository,
            UserRepository userRepository,
            TripRepository tripRepository
    ) {
        this.tripLikeRepository = tripLikeRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    // create TripLike
    public TripLike create(Integer tripId, Integer userId) {
        if (userId == null || tripId == null) {
            throw new IllegalArgumentException("userId or tripId are required");
        }

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        TripLike tripLike = new TripLike();
        tripLike.setTrip(trip);
        tripLike.setUser(user);

        return tripLikeRepository.save(tripLike);
    }

    // getAll
    @Transactional(readOnly = true)
    public List<TripLike> getAll(Integer tripId) {
        return tripLikeRepository.findAllByTrip_IdOrderByCreatedAtAsc(tripId);
    }

    // delete TripLike
    public void delete(Integer tripLikeId) {
        if (tripLikeId == null) {
            throw new IllegalArgumentException("TripLikeId is required");
        }
        if (!tripLikeRepository.existsById(tripLikeId)) {
            throw new IllegalArgumentException("TripLike not found: " + tripLikeId);
        }
        tripLikeRepository.deleteById(tripLikeId);
    }
}
