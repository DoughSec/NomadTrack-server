package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.exception.BadRequestException;
import com.nomadtrack.nomadtrackserver.exception.ForbiddenException;
import com.nomadtrack.nomadtrackserver.exception.ResourceNotFoundException;
import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.TripComment;
import com.nomadtrack.nomadtrackserver.model.dto.TripCommentResponseDto;
import com.nomadtrack.nomadtrackserver.repository.TripRepository;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.repository.TripCommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public TripCommentResponseDto create(Integer tripId, Integer userId, String comment) {
        if (userId == null || tripId == null) {
            throw new BadRequestException("userId and tripId are required");
        }

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: " + tripId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        TripComment tripComment = new TripComment();
        tripComment.setTrip(trip);
        tripComment.setUser(user);
        tripComment.setComment(comment);

        return toDto(tripCommentRepository.save(tripComment));
    }

    // getAll
    @Transactional(readOnly = true)
    public List<TripCommentResponseDto> getAll(Integer tripId) {
        return tripCommentRepository.findAllByTrip_IdOrderByCreatedAtAsc(tripId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // update TripComment - scoped to tripId
    public TripCommentResponseDto update(Integer tripId, Integer commentId, Integer userId, String comment) {
        if (tripId == null || commentId == null) {
            throw new BadRequestException("tripId and commentId are required");
        }

        TripComment tripComment = tripCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

        if (!userId.equals(tripComment.getUser().getId())) {
            throw new ForbiddenException("Cannot edit another user's comment");
        }

        TripComment existing = tripCommentRepository.findByIdAndTrip_Id(commentId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found with id " + commentId + " on trip " + tripId));

        existing.setComment(comment);
        return toDto(tripCommentRepository.save(existing));
    }

    // delete TripComment - scoped to tripId
    public void delete(Integer tripId, Integer userId, Integer commentId) {
        if (tripId == null || commentId == null) {
            throw new BadRequestException("tripId and commentId are required");
        }

        TripComment tripComment = tripCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

        if (!userId.equals(tripComment.getUser().getId())) {
            throw new ForbiddenException("Cannot delete another user's comment");
        }

        TripComment existing = tripCommentRepository.findByIdAndTrip_Id(commentId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found with id " + commentId + " on trip " + tripId));
        tripCommentRepository.delete(existing);
    }

    private TripCommentResponseDto toDto(TripComment c) {
        TripCommentResponseDto dto = new TripCommentResponseDto();
        dto.setCommentId(c.getId());
        dto.setTripId(c.getTrip().getId());
        dto.setUserId(c.getUser().getId());
        dto.setUserFirstName(c.getUser().getFirstName());
        dto.setUserLastName(c.getUser().getLastName());
        dto.setComment(c.getComment());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }
}
