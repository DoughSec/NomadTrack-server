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

    private Trip trip;
    private User user;
    private TripLike tripLike;

    @BeforeEach
    void setUp() {
        trip = new Trip();
        trip.setId(1);
        user = new User();
        user.setId(1);
        tripLike = new TripLike();
        tripLike.setId(1);
        tripLike.setTrip(trip);
        tripLike.setUser(user);
    }

    @Test
    void create_tripLike_returnsTripLike() {
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(tripLikeRepository.save(any(TripLike.class))).thenReturn(tripLike);

        TripLikeResponseDto result = tripLikeService.create(1, 1);

        assertNotNull(result);
        assertEquals(1, result.getTripId());
        assertEquals(1, result.getUserId());
        verify(tripLikeRepository).save(any(TripLike.class));
    }

    @Test
    void create_nullUserId_throws() {
        assertThrows(BadRequestException.class,
                () -> tripLikeService.create(1, null));
    }

    @Test
    void create_nullTripId_throws() {
        assertThrows(BadRequestException.class,
                () -> tripLikeService.create(null, 1));
    }

    @Test
    void create_tripNotFound_throws() {
        when(tripRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripLikeService.create(99, 1));
    }

    @Test
    void create_userNotFound_throws() {
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripLikeService.create(1, 99));
    }

    @Test
    void getAll_returnsList() {
        when(tripLikeRepository.findAllByTrip_IdOrderByCreatedAtAsc(1))
                .thenReturn(List.of(tripLike));

        List<TripLikeResponseDto> results = tripLikeService.getAll(1);

        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getTripId());
    }

    @Test
    void delete_success() {
        when(tripLikeRepository.findByIdAndTrip_Id(1, 1)).thenReturn(Optional.of(tripLike));

        tripLikeService.delete(1, 1);

        verify(tripLikeRepository).delete(tripLike);
    }

    @Test
    void delete_nullId_throws() {
        assertThrows(BadRequestException.class,
                () -> tripLikeService.delete(1, null));
    }

    @Test
    void delete_notFound_throws() {
        when(tripLikeRepository.findByIdAndTrip_Id(99, 1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripLikeService.delete(1, 99));
    }
}
