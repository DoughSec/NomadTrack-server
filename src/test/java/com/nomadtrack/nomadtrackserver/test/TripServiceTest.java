package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.exception.BadRequestException;
import com.nomadtrack.nomadtrackserver.exception.ForbiddenException;
import com.nomadtrack.nomadtrackserver.exception.ResourceNotFoundException;
import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.MapPinDto;
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
import java.util.ArrayList;
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
        trip.setComments(new ArrayList<>());
        trip.setLikes(new ArrayList<>());
        trip.setPhotos(new ArrayList<>());
    }

    // --- create() ---

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
    void create_dtoContainsCorrectFields() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        TripResponseDto result = tripService.create(1, "testTitle", "testCity", "testCountry",
                LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2),
                "testNotes", new BigDecimal(1), new BigDecimal(1), "Public");

        assertEquals("testTitle", result.getTitle());
        assertEquals("testCity", result.getCity());
        assertEquals("testCountry", result.getCountry());
        assertEquals(1, result.getUserId());
    }

    @Test
    void create_nullUserId_throws() {
        assertThrows(BadRequestException.class,
                () -> tripService.create(null, "test", "Columbus", "United States",
                        LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2),
                        "notes", new BigDecimal(1), new BigDecimal(1), "Public"));
    }

    @Test
    void create_userNotFound_throws() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripService.create(99, "test", "Columbus", "United States",
                        LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2),
                        "notes", new BigDecimal(1), new BigDecimal(1), "Public"));
    }

    // --- getAllMyTrips() ---

    @Test
    void getAllMyTrips_returnsList() {
        when(tripRepository.findAll()).thenReturn(List.of(trip));

        List<TripResponseDto> results = tripService.getAllMyTrips(1);

        assertEquals(1, results.size());
    }

    @Test
    void getAllMyTrips_filtersOtherUsers() {
        User otherUser = new User();
        otherUser.setId(2);
        Trip otherTrip = new Trip();
        otherTrip.setId(2);
        otherTrip.setUser(otherUser);

        when(tripRepository.findAll()).thenReturn(List.of(trip, otherTrip));

        List<TripResponseDto> results = tripService.getAllMyTrips(1);

        assertEquals(1, results.size());
        assertEquals(1, results.getFirst().getUserId());
    }

    @Test
    void getAllMyTrips_emptyWhenNoMatch() {
        User otherUser = new User();
        otherUser.setId(2);
        Trip otherTrip = new Trip();
        otherTrip.setId(2);
        otherTrip.setUser(otherUser);

        when(tripRepository.findAll()).thenReturn(List.of(otherTrip));

        List<TripResponseDto> results = tripService.getAllMyTrips(1);

        assertTrue(results.isEmpty());
    }

    // --- getAllUserTrips() ---

    @Test
    void getAllUserTrips_returnsList() {
        when(tripRepository.findAll()).thenReturn(List.of(trip));

        List<TripResponseDto> results = tripService.getAllUserTrips(1);

        assertEquals(1, results.size());
    }

    @Test
    void getAllUserTrips_filtersOtherUsers() {
        User otherUser = new User();
        otherUser.setId(2);
        Trip otherTrip = new Trip();
        otherTrip.setId(2);
        otherTrip.setUser(otherUser);

        when(tripRepository.findAll()).thenReturn(List.of(trip, otherTrip));

        List<TripResponseDto> results = tripService.getAllUserTrips(1);

        assertEquals(1, results.size());
    }

    // --- getById() ---

    @Test
    void getById_success() {
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));

        Trip result = tripService.getById(1);

        assertEquals(1, result.getId());
    }

    @Test
    void getById_nullId_throws() {
        assertThrows(BadRequestException.class,
                () -> tripService.getById(null));
    }

    @Test
    void getById_notFound_throws() {
        when(tripRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripService.getById(99));
    }

    // --- getByCountryName() ---

    @Test
    void getByCountryName_success() {
        when(tripRepository.findByCountryIgnoreCase("testCountry")).thenReturn(List.of(trip));

        var results = tripService.getByCountryName("testCountry");

        assertEquals(1, results.size());
        assertEquals("testCountry", results.getFirst().getCountry());
    }

    @Test
    void getByCountryName_nullCountry_throws() {
        assertThrows(BadRequestException.class,
                () -> tripService.getByCountryName(null));
    }

    @Test
    void getByCountryName_noResults_returnsEmpty() {
        when(tripRepository.findByCountryIgnoreCase("Nowhere")).thenReturn(List.of());

        var results = tripService.getByCountryName("Nowhere");

        assertTrue(results.isEmpty());
    }


    // --- update() ---

    @Test
    void update_success() {
        TripRequestDto dto = new TripRequestDto();
        dto.setTitle("updatedTitle");
        dto.setCity("updatedCity");
        dto.setCountry("updatedCountry");
        dto.setStartDate(LocalDate.of(2021, 3, 1));
        dto.setEndDate(LocalDate.of(2021, 3, 10));
        dto.setNotes("updatedNotes");
        dto.setLatitude(new BigDecimal("2.5"));
        dto.setLongitude(new BigDecimal("3.5"));
        dto.setVisibility("Private");

        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        TripResponseDto result = tripService.update(1, 1, dto);

        assertEquals("updatedTitle", result.getTitle());
        assertEquals("updatedCity", result.getCity());
        assertEquals("Private", result.getVisibility());
        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    void update_wrongOwner_throws() {
        TripRequestDto dto = new TripRequestDto();
        dto.setTitle("x");

        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));

        assertThrows(ForbiddenException.class,
                () -> tripService.update(1, 99, dto));
    }

    @Test
    void update_tripNotFound_throws() {
        TripRequestDto dto = new TripRequestDto();
        when(tripRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripService.update(99, 1, dto));
    }

    // --- delete() ---

    @Test
    void delete_success() {
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));

        tripService.delete(1);

        verify(tripRepository).delete(trip);
    }

    @Test
    void delete_nullId_throws() {
        assertThrows(BadRequestException.class,
                () -> tripService.delete(null));
    }

    @Test
    void delete_notFound_throws() {
        when(tripRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripService.delete(99));
    }
}
