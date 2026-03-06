package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.exception.BadRequestException;
import com.nomadtrack.nomadtrackserver.exception.ForbiddenException;
import com.nomadtrack.nomadtrackserver.exception.ResourceNotFoundException;
import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.TripPhoto;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.TripPhotoResponseDto;
import com.nomadtrack.nomadtrackserver.repository.TripPhotoRepository;
import com.nomadtrack.nomadtrackserver.repository.TripRepository;
import com.nomadtrack.nomadtrackserver.service.TripPhotoService;
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
public class TripPhotoServiceTest {

    @Mock
    private TripRepository tripRepository;
    @Mock
    private TripPhotoRepository tripPhotoRepository;

    @InjectMocks
    private TripPhotoService tripPhotoService;

    private User user;
    private Trip trip;
    private TripPhoto tripPhoto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);

        trip = new Trip();
        trip.setId(1);
        trip.setUser(user);

        tripPhoto = new TripPhoto();
        tripPhoto.setId(1);
        tripPhoto.setTrip(trip);
        tripPhoto.setUrl("https://test.com/photo.jpg");
        tripPhoto.setCaption("this is a caption");
        tripPhoto.setSortOrder(1);
    }

    // --- create() ---

    @Test
    void create_success() {
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));
        when(tripPhotoRepository.save(any(TripPhoto.class))).thenReturn(tripPhoto);

        TripPhoto result = tripPhotoService.create(1, "https://test.com", "Fun trip!", 1);

        assertNotNull(result);
        verify(tripPhotoRepository).save(any(TripPhoto.class));
    }

    @Test
    void create_setsFieldsCorrectly() {
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));
        when(tripPhotoRepository.save(any(TripPhoto.class))).thenReturn(tripPhoto);

        TripPhoto result = tripPhotoService.create(1, "https://test.com/photo.jpg", "caption", 2);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void create_nullTripId_throws() {
        assertThrows(BadRequestException.class,
                () -> tripPhotoService.create(null, "test", "comment", 1));
    }

    @Test
    void create_tripNotFound_throws() {
        when(tripRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripPhotoService.create(99, "test", "comment", 1));
    }

    @Test
    void createForUser_wrongUser_throws() {
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));

        // userId=99 does not own trip (owned by user id=1)
        assertThrows(ForbiddenException.class,
                () -> tripPhotoService.createForUser(1, 99, "https://url.com", "cap", 1));
    }

    @Test
    void createForUser_nullTripId_throws() {
        assertThrows(BadRequestException.class,
                () -> tripPhotoService.createForUser(null, 1, "https://url.com", "cap", 1));
    }

    @Test
    void createForUser_tripNotFound_throws() {
        when(tripRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripPhotoService.createForUser(99, 1, "https://url.com", "cap", 1));
    }

    @Test
    void createForUser_dtoContainsCorrectFields() {
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));
        when(tripPhotoRepository.save(any(TripPhoto.class))).thenReturn(tripPhoto);

        TripPhotoResponseDto result = tripPhotoService.createForUser(1, 1, "https://test.com/photo.jpg", "this is a caption", 1);

        assertEquals("this is a caption", result.getCaption());
        assertEquals(1, result.getSortOrder());
    }

    // --- getAll() ---

    @Test
    void getAll_returnsList() {
        when(tripPhotoRepository.findAllByTrip_IdOrderByCreatedAtAsc(1))
                .thenReturn(List.of(tripPhoto));

        List<TripPhoto> results = tripPhotoService.getAll(1);

        assertEquals(1, results.size());
    }

    @Test
    void getAll_returnsEmptyList() {
        when(tripPhotoRepository.findAllByTrip_IdOrderByCreatedAtAsc(1))
                .thenReturn(List.of());

        List<TripPhoto> results = tripPhotoService.getAll(1);

        assertTrue(results.isEmpty());
    }

    @Test
    void getAll_returnsMultiple() {
        TripPhoto photo2 = new TripPhoto();
        photo2.setId(2);
        photo2.setTrip(trip);

        when(tripPhotoRepository.findAllByTrip_IdOrderByCreatedAtAsc(1))
                .thenReturn(List.of(tripPhoto, photo2));

        List<TripPhoto> results = tripPhotoService.getAll(1);

        assertEquals(2, results.size());
    }

    // --- getAllByUserId() ---

    @Test
    void getAllByUserId_success() {
        when(tripRepository.findByUser_Id(1)).thenReturn(List.of(trip));
        when(tripPhotoRepository.findAllByTrip_IdOrderByCreatedAtAsc(1))
                .thenReturn(List.of(tripPhoto));

        List<TripPhotoResponseDto> results = tripPhotoService.getAllByUserId(1, 1);

        assertEquals(1, results.size());
        assertEquals(1, results.getFirst().getPhotoId());
    }

    @Test
    void getAllByUserId_tripNotOwnedByUser_throws() {
        // user owns no trips
        when(tripRepository.findByUser_Id(99)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> tripPhotoService.getAllByUserId(1, 99));
    }

    @Test
    void getAllByUserId_dtoFieldsCorrect() {
        when(tripRepository.findByUser_Id(1)).thenReturn(List.of(trip));
        when(tripPhotoRepository.findAllByTrip_IdOrderByCreatedAtAsc(1))
                .thenReturn(List.of(tripPhoto));

        List<TripPhotoResponseDto> results = tripPhotoService.getAllByUserId(1, 1);

        assertEquals("https://test.com/photo.jpg", results.getFirst().getUrl());
        assertEquals("this is a caption", results.getFirst().getCaption());
        assertEquals(1, results.getFirst().getSortOrder());
    }

    // --- delete() ---

    @Test
    void delete_success() {
        when(tripPhotoRepository.existsById(1)).thenReturn(true);

        tripPhotoService.delete(1);

        verify(tripPhotoRepository).deleteById(1);
    }

    @Test
    void delete_nullId_throws() {
        assertThrows(BadRequestException.class,
                () -> tripPhotoService.delete(null));
    }

    @Test
    void delete_notFound_throws() {
        when(tripPhotoRepository.existsById(99)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> tripPhotoService.delete(99));
    }

    // --- deleteForUser() ---

    @Test
    void deleteForUser_success() {
        when(tripPhotoRepository.findById(1)).thenReturn(Optional.of(tripPhoto));

        tripPhotoService.deleteForUser(1, 1);

        verify(tripPhotoRepository).deleteById(1);
    }

    @Test
    void deleteForUser_wrongUser_throws() {
        when(tripPhotoRepository.findById(1)).thenReturn(Optional.of(tripPhoto));

        assertThrows(ForbiddenException.class,
                () -> tripPhotoService.deleteForUser(1, 99));
    }
}
