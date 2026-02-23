package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.TripPhoto;
import com.nomadtrack.nomadtrackserver.model.dto.TripPhotoDto;
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
    public TripPhoto create(@PathVariable Integer tripId, @RequestBody TripPhotoDto request) {
        return tripPhotoService.create(
                tripId,
                request.getUrl(),
                request.getCaption(),
                request.getSortOrder()
        );
    }

    //get all TripPhoto records
    @GetMapping("/{tripId}/photos")
    @ResponseStatus(HttpStatus.OK)
    public List<TripPhoto> getAll(@PathVariable("tripId") Integer tripId) {
        return tripPhotoService.getAll(tripId);
    }

    //delete TripPhoto record
    @DeleteMapping("/photos/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTripPhoto(@PathVariable("photoId") Integer photoId) {
        tripPhotoService.delete(photoId);
    }

}
