package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.dto.TripLikeResponseDto;
import com.nomadtrack.nomadtrackserver.security.SecurityUtils;
import com.nomadtrack.nomadtrackserver.service.TripLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nomadTrack/trips")
public class TripLikeController {
    private final TripLikeService tripLikeService;

    public TripLikeController(TripLikeService tripLikeService) {
        this.tripLikeService = tripLikeService;
    }

    //create TripLike record
    @PostMapping("/{tripId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public TripLikeResponseDto create(@PathVariable Integer tripId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return tripLikeService.create(tripId, currentUserId.intValue());
    }

    //get all TripLike records
    @GetMapping("/{tripId}/likes")
    @ResponseStatus(HttpStatus.OK)
    public List<TripLikeResponseDto> getAll(@PathVariable("tripId") Integer tripId) {
        return tripLikeService.getAll(tripId);
    }

    //delete TripLike record
    @DeleteMapping("/{tripId}/likes/{likeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTripLike(@PathVariable("tripId") Integer tripId,
                                @PathVariable("likeId") Integer likeId) {
        tripLikeService.delete(tripId, likeId);
    }
}
