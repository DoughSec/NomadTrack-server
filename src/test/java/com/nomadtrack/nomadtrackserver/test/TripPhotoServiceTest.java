package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.exception.BadRequestException;
import com.nomadtrack.nomadtrackserver.exception.ResourceNotFoundException;
import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.TripPhoto;
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

    private Trip trip;
    private TripPhoto tripPhoto;

    @BeforeEach
    void setUp() {
        trip = new Trip();
        trip.setId(1);
        tripPhoto = new TripPhoto();
        tripPhoto.setId(1);
        tripPhoto.setTrip(trip);
        tripPhoto.setUrl("test");
        tripPhoto.setCaption("this is a caption");
        tripPhoto.setSortOrder(1);
    }

    @Test
    void create_success() {
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));
        when(tripPhotoRepository.save(any(TripPhoto.class))).thenReturn(tripPhoto);

        TripPhoto result = tripPhotoService.create(1, "https://test.com", "Fun trip!", 1);

        assertNotNull(result);
        verify(tripPhotoRepository).save(any(TripPhoto.class));
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
    void getAll_returnsList() {
        when(tripPhotoRepository.findAllByTrip_IdOrderByCreatedAtAsc(1))
                .thenReturn(List.of(tripPhoto));

        List<TripPhoto> results = tripPhotoService.getAll(1);

        assertEquals(1, results.size());
    }

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
}
