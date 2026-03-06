package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.exception.BadRequestException;
import com.nomadtrack.nomadtrackserver.exception.ForbiddenException;
import com.nomadtrack.nomadtrackserver.exception.ResourceNotFoundException;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripCommentServiceTest {

    @Mock
    private TripCommentRepository tripCommentRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TripCommentService tripCommentService;

    private User user;
    private Trip trip;
    private TripComment comment;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");

        trip = new Trip();
        trip.setId(10);

        comment = new TripComment();
        comment.setId(100);
        comment.setTrip(trip);
        comment.setUser(user);
        comment.setComment("Hello World");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
    }

    // --- create() ---

    @Test
    void create_success() {
        when(tripRepository.findById(10)).thenReturn(Optional.of(trip));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(tripCommentRepository.save(any(TripComment.class))).thenReturn(comment);

        TripCommentResponseDto result = tripCommentService.create(10, 1, "Hello World");

        assertNotNull(result);
        assertEquals(100, result.getCommentId());
        assertEquals(10, result.getTripId());
        assertEquals(1, result.getUserId());
        assertEquals("Hello World", result.getComment());
    }

    @Test
    void create_dtoContainsUserName() {
        when(tripRepository.findById(10)).thenReturn(Optional.of(trip));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(tripCommentRepository.save(any(TripComment.class))).thenReturn(comment);

        TripCommentResponseDto result = tripCommentService.create(10, 1, "Hello World");

        assertEquals("John", result.getUserFirstName());
        assertEquals("Doe", result.getUserLastName());
    }

    @Test
    void create_nullUserId_throws() {
        assertThrows(BadRequestException.class, () -> tripCommentService.create(10, null, "text"));
    }

    @Test
    void create_nullTripId_throws() {
        assertThrows(BadRequestException.class, () -> tripCommentService.create(null, 1, "text"));
    }

    @Test
    void create_tripNotFound_throws() {
        when(tripRepository.findById(10)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tripCommentService.create(10, 1, "text"));
    }

    @Test
    void create_userNotFound_throws() {
        when(tripRepository.findById(10)).thenReturn(Optional.of(trip));
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tripCommentService.create(10, 1, "text"));
    }

    // --- getAll() ---

    @Test
    void getAll_returnsList() {
        when(tripCommentRepository.findAllByTrip_IdOrderByCreatedAtAsc(10)).thenReturn(List.of(comment));

        List<TripCommentResponseDto> result = tripCommentService.getAll(10);

        assertEquals(1, result.size());
        assertEquals(100, result.getFirst().getCommentId());
    }

    @Test
    void getAll_returnsEmptyList() {
        when(tripCommentRepository.findAllByTrip_IdOrderByCreatedAtAsc(10)).thenReturn(List.of());

        List<TripCommentResponseDto> result = tripCommentService.getAll(10);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_multipleComments() {
        TripComment c2 = new TripComment();
        c2.setId(101);
        c2.setTrip(trip);
        c2.setUser(user);
        c2.setComment("Second comment");
        c2.setCreatedAt(LocalDateTime.now());
        c2.setUpdatedAt(LocalDateTime.now());

        when(tripCommentRepository.findAllByTrip_IdOrderByCreatedAtAsc(10)).thenReturn(List.of(comment, c2));

        List<TripCommentResponseDto> result = tripCommentService.getAll(10);

        assertEquals(2, result.size());
    }

    // --- update() ---

    @Test
    void update_success() {
        when(tripCommentRepository.findById(100)).thenReturn(Optional.of(comment));
        when(tripCommentRepository.findByIdAndTrip_Id(100, 10)).thenReturn(Optional.of(comment));
        when(tripCommentRepository.save(any(TripComment.class))).thenReturn(comment);

        TripCommentResponseDto result = tripCommentService.update(10, 100, 1, "Updated text");

        assertNotNull(result);
        verify(tripCommentRepository).save(comment);
    }

    @Test
    void update_nullTripId_throws() {
        assertThrows(BadRequestException.class, () -> tripCommentService.update(null, 100, 1, "text"));
    }

    @Test
    void update_nullCommentId_throws() {
        assertThrows(BadRequestException.class, () -> tripCommentService.update(10, null, 1, "text"));
    }

    @Test
    void update_commentNotFound_throws() {
        when(tripCommentRepository.findById(100)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tripCommentService.update(10, 100, 1, "text"));
    }

    @Test
    void update_forbiddenUser_throws() {
        when(tripCommentRepository.findById(100)).thenReturn(Optional.of(comment));
        assertThrows(ForbiddenException.class, () -> tripCommentService.update(10, 100, 99, "text"));
    }

    @Test
    void update_commentNotOnTrip_throws() {
        when(tripCommentRepository.findById(100)).thenReturn(Optional.of(comment));
        when(tripCommentRepository.findByIdAndTrip_Id(100, 10)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tripCommentService.update(10, 100, 1, "text"));
    }

    // --- delete() ---

    @Test
    void delete_success() {
        when(tripCommentRepository.findById(100)).thenReturn(Optional.of(comment));
        when(tripCommentRepository.findByIdAndTrip_Id(100, 10)).thenReturn(Optional.of(comment));

        tripCommentService.delete(10, 1, 100);

        verify(tripCommentRepository).delete(comment);
    }

    @Test
    void delete_nullTripId_throws() {
        assertThrows(BadRequestException.class, () -> tripCommentService.delete(null, 1, 100));
    }

    @Test
    void delete_nullCommentId_throws() {
        assertThrows(BadRequestException.class, () -> tripCommentService.delete(10, 1, null));
    }

    @Test
    void delete_commentNotFound_throws() {
        when(tripCommentRepository.findById(100)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tripCommentService.delete(10, 1, 100));
    }

}
