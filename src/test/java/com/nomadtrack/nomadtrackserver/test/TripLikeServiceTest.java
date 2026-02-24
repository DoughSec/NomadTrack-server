package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.TripLike;
import com.nomadtrack.nomadtrackserver.model.User;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        TripLike result = tripLikeService.create(1, 1);

        assertNotNull(result);
        verify(tripLikeRepository).save(any(TripLike.class));
    }

    @Test
    void create_nullUserId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> tripLikeService.create(1, null));
    }

    @Test
    void create_nullTripId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> tripLikeService.create(null, 1));
    }

    @Test
    void create_tripNotFound_throws() {
        when(tripRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> tripLikeService.create(99, 1));
    }

    @Test
    void create_userNotFound_throws() {
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> tripLikeService.create(1, 99));
    }

    @Test
    void getAll_returnsList() {
        when(tripLikeRepository.findAllByTrip_IdOrderByCreatedAtAsc(1))
                .thenReturn(List.of(tripLike));

        List<TripLike> results = tripLikeService.getAll(1);

        assertEquals(1, results.size());
    }

    @Test
    void delete_success() {
        when(tripLikeRepository.existsById(1)).thenReturn(true);

        tripLikeService.delete(1);

        verify(tripLikeRepository).deleteById(1);
    }

    @Test
    void delete_nullId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> tripLikeService.delete(null));
    }

    @Test
    void delete_notFound_throws() {
        when(tripLikeRepository.existsById(99)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> tripLikeService.delete(99));
    }
}
