package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.exception.BadRequestException;
import com.nomadtrack.nomadtrackserver.exception.ResourceNotFoundException;
import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.TripLike;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.TripLikeResponseDto;
import com.nomadtrack.nomadtrackserver.repository.TripLikeRepository;
import com.nomadtrack.nomadtrackserver.repository.TripRepository;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.service.TripLikeService;
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
public class TripLikeServiceTest {

    @Mock
    private TripLikeRepository tripLikeRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TripLikeService tripLikeService;

    private User user;
    private Trip trip;
    private TripLike tripLike;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFirstName("Jane");
        user.setLastName("Doe");

        trip = new Trip();
        trip.setId(10);

        tripLike = new TripLike();
        tripLike.setId(50);
        tripLike.setTrip(trip);
        tripLike.setUser(user);
        tripLike.setCreatedAt(LocalDateTime.now());
    }

    // --- create() ---

    @Test
    void create_success() {
        when(tripRepository.findById(10)).thenReturn(Optional.of(trip));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(tripLikeRepository.save(any(TripLike.class))).thenReturn(tripLike);

        TripLikeResponseDto result = tripLikeService.create(10, 1);

        assertNotNull(result);
        assertEquals(50, result.getLikeId());
        assertEquals(10, result.getTripId());
        assertEquals(1, result.getUserId());
    }

    @Test
    void create_dtoContainsUserName() {
        when(tripRepository.findById(10)).thenReturn(Optional.of(trip));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(tripLikeRepository.save(any(TripLike.class))).thenReturn(tripLike);

        TripLikeResponseDto result = tripLikeService.create(10, 1);

        assertEquals("Jane", result.getUserFirstName());
        assertEquals("Doe", result.getUserLastName());
    }

    @Test
    void create_nullTripId_throws() {
        assertThrows(BadRequestException.class, () -> tripLikeService.create(null, 1));
    }

    @Test
    void create_nullUserId_throws() {
        assertThrows(BadRequestException.class, () -> tripLikeService.create(10, null));
    }

    @Test
    void create_tripNotFound_throws() {
        when(tripRepository.findById(10)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tripLikeService.create(10, 1));
    }

    @Test
    void create_userNotFound_throws() {
        when(tripRepository.findById(10)).thenReturn(Optional.of(trip));
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tripLikeService.create(10, 1));
    }

    // --- getAll() ---

    @Test
    void getAll_returnsList() {
        when(tripLikeRepository.findAllByTrip_IdOrderByCreatedAtAsc(10)).thenReturn(List.of(tripLike));

        List<TripLikeResponseDto> result = tripLikeService.getAll(10);

        assertEquals(1, result.size());
        assertEquals(50, result.getFirst().getLikeId());
    }

    @Test
    void getAll_returnsEmptyList() {
        when(tripLikeRepository.findAllByTrip_IdOrderByCreatedAtAsc(10)).thenReturn(List.of());

        List<TripLikeResponseDto> result = tripLikeService.getAll(10);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_multipleResults() {
        TripLike like2 = new TripLike();
        like2.setId(51);
        like2.setTrip(trip);
        like2.setUser(user);
        like2.setCreatedAt(LocalDateTime.now());

        when(tripLikeRepository.findAllByTrip_IdOrderByCreatedAtAsc(10)).thenReturn(List.of(tripLike, like2));

        List<TripLikeResponseDto> result = tripLikeService.getAll(10);

        assertEquals(2, result.size());
    }

    // --- delete() ---

    @Test
    void delete_success() {
        when(tripLikeRepository.findByIdAndTrip_Id(50, 10)).thenReturn(Optional.of(tripLike));

        tripLikeService.delete(10, 50);

        verify(tripLikeRepository).delete(tripLike);
    }

    @Test
    void delete_nullTripId_throws() {
        assertThrows(BadRequestException.class, () -> tripLikeService.delete(null, 50));
    }

    @Test
    void delete_nullLikeId_throws() {
        assertThrows(BadRequestException.class, () -> tripLikeService.delete(10, null));
    }

}
