package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.TripComment;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.TripCommentResponseDto;
import com.nomadtrack.nomadtrackserver.repository.TripCommentRepository;
import com.nomadtrack.nomadtrackserver.repository.TripRepository;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.service.TripCommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripCommentServiceTest {
    @Mock
    private TripRepository tripRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TripCommentRepository tripCommentRepository;

    @InjectMocks
    private TripCommentService tripCommentService;

    private Trip trip;
    private User user;
    private TripComment tripComment;

    @BeforeEach
    void setUp() {
        trip = new Trip();
        trip.setId(1);
        user = new User();
        user.setId(1);
        tripComment = new TripComment();
        tripComment.setId(1);
        tripComment.setTrip(trip);
        tripComment.setUser(user);
        tripComment.setComment("Nice trip!");
    }

    @Test
    void create_success() {
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(tripCommentRepository.save(any(TripComment.class))).thenReturn(tripComment);

        TripCommentResponseDto result = tripCommentService.create(1, 1, "Nice trip!");

        assertNotNull(result);
        assertEquals("Nice trip!", result.getComment());
        assertEquals(1, result.getTripId());
        assertEquals(1, result.getUserId());
        verify(tripCommentRepository).save(any(TripComment.class));
    }

    @Test
    void create_nullUserId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> tripCommentService.create(1, null, "comment"));
    }

    @Test
    void create_nullTripId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> tripCommentService.create(null, 1, "comment"));
    }

    @Test
    void create_tripNotFound_throws() {
        when(tripRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> tripCommentService.create(99, 1, "comment"));
    }

    @Test
    void getAll_returnsList() {
        when(tripCommentRepository.findAllByTrip_IdOrderByCreatedAtAsc(1))
                .thenReturn(List.of(tripComment));

        List<TripCommentResponseDto> results = tripCommentService.getAll(1);

        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getTripId());
    }

    @Test
    void update_success() {
        when(tripCommentRepository.findById(1)).thenReturn(Optional.of(tripComment));
        when(tripCommentRepository.findByIdAndTrip_Id(1, 1)).thenReturn(Optional.of(tripComment));
        when(tripCommentRepository.save(any(TripComment.class))).thenReturn(tripComment);

        // update(tripId, commentId, userId, comment)
        TripCommentResponseDto result = tripCommentService.update(1, 1, 1, "Updated comment");

        assertEquals("Updated comment", result.getComment());
        verify(tripCommentRepository).save(any(TripComment.class));
    }

    @Test
    void update_notFound_throws() {
        when(tripCommentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> tripCommentService.update(1, 99, 1, "updated"));
    }

    @Test
    void delete_success() {
        when(tripCommentRepository.findById(1)).thenReturn(Optional.of(tripComment));
        when(tripCommentRepository.findByIdAndTrip_Id(1, 1)).thenReturn(Optional.of(tripComment));

        // delete(tripId, userId, commentId)
        tripCommentService.delete(1, 1, 1);

        verify(tripCommentRepository).delete(tripComment);
    }

    @Test
    void delete_nullId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> tripCommentService.delete(1, 1, null));
    }

    @Test
    void delete_notFound_throws() {
        when(tripCommentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> tripCommentService.delete(1, 1, 99));
    }
}
