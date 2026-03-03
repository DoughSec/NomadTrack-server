package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.TripRequestDto;
import com.nomadtrack.nomadtrackserver.model.dto.TripResponseDto;
import com.nomadtrack.nomadtrackserver.repository.TripRepository;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {
    @Mock
    private TripRepository tripRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TripService tripService;

    private Trip trip;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        trip = new Trip();
        trip.setId(1);
        trip.setUser(user);
        trip.setTitle("testTitle");
        trip.setCity("testCity");
        trip.setCountry("testCountry");
        trip.setStartDate(LocalDate.of(2020, 1, 1));
        trip.setEndDate(LocalDate.of(2020, 1, 2));
        trip.setNotes("testNotes");
        trip.setLatitude(new BigDecimal(1));
        trip.setLongitude(new BigDecimal(1));
        trip.setVisibility("Public");
    }

    @Test
    void create_success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        TripResponseDto result = tripService.create(1, "test", "Columbus", "United States",
                LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2),
                "notes", new BigDecimal(1), new BigDecimal(1), "Public");

        assertNotNull(result);
        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    void create_nullUserId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> tripService.create(null, "test", "Columbus", "United States",
                        LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2),
                        "notes", new BigDecimal(1), new BigDecimal(1), "Public"));
    }

    @Test
    void create_userNotFound_throws() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> tripService.create(99, "test", "Columbus", "United States",
                        LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2),
                        "notes", new BigDecimal(1), new BigDecimal(1), "Public"));
    }

    @Test
    void getAll_returnsList() {
        when(tripRepository.findAll()).thenReturn(List.of(trip));

        List<TripResponseDto> results = tripService.getAllMyTrips(1);

        assertEquals(1, results.size());
    }

    @Test
    void getById_success() {
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));

        Trip result = tripService.getById(1);

        assertEquals(1, result.getId());
    }

    @Test
    void getById_nullId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> tripService.getById(null));
    }

    @Test
    void getMapPins_returnsPins() {
        when(tripRepository.findAll()).thenReturn(List.of(trip));

        var pins = tripService.getMapPins();

        assertEquals(1, pins.size());
        assertEquals("testCity", pins.get(0).getCity());
    }

    @Test
    void getMapPins_skipsNullCoordinates() {
        Trip noCoords = new Trip();
        noCoords.setId(2);
        noCoords.setCity("NoCoords");

        when(tripRepository.findAll()).thenReturn(List.of(trip, noCoords));

        var pins = tripService.getMapPins();

        assertEquals(1, pins.size());
    }

    @Test
    void update_success() {
        TripRequestDto dto = new TripRequestDto();
        dto.setTitle("testTitle");
        dto.setCity("testCity");
        dto.setCountry("testCountry");
        dto.setStartDate(LocalDate.of(2020, 1, 1));
        dto.setEndDate(LocalDate.of(2020, 1, 2));
        dto.setNotes("testNotes");
        dto.setLatitude(new BigDecimal(1));
        dto.setLongitude(new BigDecimal(1));
        dto.setVisibility("Public");

        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        TripResponseDto result = tripService.update(1, 1, dto);

        assertEquals("testTitle", result.getTitle());
        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    void delete_success() {
        when(tripRepository.existsById(1)).thenReturn(true);

        tripService.delete(1);

        verify(tripRepository).deleteById(1);
    }

    @Test
    void delete_nullId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> tripService.delete(null));
    }

    @Test
    void delete_notFound_throws() {
        when(tripRepository.existsById(99)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> tripService.delete(99));
    }
}
