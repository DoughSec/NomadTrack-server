package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.TripLike;
import com.nomadtrack.nomadtrackserver.model.dto.TripLikeResponseDto;
import com.nomadtrack.nomadtrackserver.repository.TripRepository;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.repository.TripLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public TripLikeResponseDto create(Integer tripId, Integer userId) {
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

        return toDto(tripLikeRepository.save(tripLike));
    }

    // getAll
    @Transactional(readOnly = true)
    public List<TripLikeResponseDto> getAll(Integer tripId) {
        return tripLikeRepository.findAllByTrip_IdOrderByCreatedAtAsc(tripId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // delete TripLike
    public void delete(Integer tripId, Integer likeId) {
        if (tripId == null || likeId == null) {
            throw new IllegalArgumentException("tripId and likeId are required");
        }
        TripLike tripLike = tripLikeRepository.findByIdAndTrip_Id(likeId, tripId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Like not found with id " + likeId + " on trip " + tripId));
        tripLikeRepository.delete(tripLike);
    }

    private TripLikeResponseDto toDto(TripLike tripLike) {
        TripLikeResponseDto dto = new TripLikeResponseDto();
        dto.setLikeId(tripLike.getId());
        dto.setTripId(tripLike.getTrip().getId());
        dto.setUserId(tripLike.getUser().getId());
        dto.setUserFirstName(tripLike.getUser().getFirstName());
        dto.setUserLastName(tripLike.getUser().getLastName());
        dto.setCreatedAt(tripLike.getCreatedAt());
        return dto;
    }
}
