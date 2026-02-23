package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.TripComment;
import com.nomadtrack.nomadtrackserver.model.dto.CommentRequest;
import com.nomadtrack.nomadtrackserver.repository.TripRepository;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.repository.TripCommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TripCommentService {

    private final TripCommentRepository tripCommentRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public TripCommentService(
            TripCommentRepository tripCommentRepository,
            UserRepository userRepository,
            TripRepository tripRepository
    ) {
        this.tripCommentRepository = tripCommentRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    // create TripComment
    public TripComment create(
            Integer tripId, Integer userId, String comment
    ) {
        if (userId == null || tripId == null) {
            throw new IllegalArgumentException("userId or tripId are required");
        }

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        TripComment tripComment = new TripComment();
        tripComment.setTrip(trip);
        tripComment.setUser(user);
        tripComment.setComment(comment);

        return tripCommentRepository.save(tripComment);
    }

    // getAll
    @Transactional(readOnly = true)
    public List<TripComment> getAll(Integer tripId) {
        return tripCommentRepository.findAllByTrip_IdOrderByCreatedAtAsc(tripId);
    }

    // getById
    @Transactional(readOnly = true)
    public TripComment getById(Integer tripCommentId) {
        if (tripCommentId == null) {
            throw new IllegalArgumentException("tripCommentId is required");
        }
        return tripCommentRepository.findById(tripCommentId)
                .orElseThrow(() -> new IllegalArgumentException("tripCommentId not found: " + tripCommentId));
    }

    // update TripComment
    public TripComment update(Integer tripCommentId, String comment) {
        TripComment existing = getById(tripCommentId);

        existing.setComment(comment);

        return tripCommentRepository.save(existing);
    }

    // delete TripComment
    public void delete(Integer tripCommentId) {
        if (tripCommentId == null) {
            throw new IllegalArgumentException("TripCommentId is required");
        }
        if (!tripCommentRepository.existsById(tripCommentId)) {
            throw new IllegalArgumentException("TripComment not found: " + tripCommentId);
        }
        tripCommentRepository.deleteById(tripCommentId);
    }
}
