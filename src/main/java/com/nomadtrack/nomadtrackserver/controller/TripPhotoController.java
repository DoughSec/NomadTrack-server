package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.dto.TripPhotoDto;
import com.nomadtrack.nomadtrackserver.model.dto.TripPhotoResponseDto;
import com.nomadtrack.nomadtrackserver.security.SecurityUtils;
import com.nomadtrack.nomadtrackserver.service.TripPhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nomadTrack/trips")
public class TripPhotoController {
    private final TripPhotoService tripPhotoService;

    public TripPhotoController(TripPhotoService tripPhotoService) {
        this.tripPhotoService = tripPhotoService;
    }

    //create TripPhoto record
    @PostMapping("/{tripId}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    public TripPhotoResponseDto create(@PathVariable Integer tripId, @RequestBody TripPhotoDto request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return tripPhotoService.createForUser(
                tripId,
                currentUserId.intValue(),
                request.getUrl(),
                request.getCaption(),
                request.getSortOrder()
        );
    }

    //get all TripPhoto records for the current user's trip
    @GetMapping("/{tripId}/photos")
    @ResponseStatus(HttpStatus.OK)
    public List<TripPhotoResponseDto> getAll(@PathVariable("tripId") Integer tripId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return tripPhotoService.getAllByUserId(tripId, currentUserId.intValue());
    }

    //delete TripPhoto record
    @DeleteMapping("/photos/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTripPhoto(
                                @PathVariable("photoId") Integer photoId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        tripPhotoService.deleteForUser(photoId, currentUserId.intValue());
    }
}
