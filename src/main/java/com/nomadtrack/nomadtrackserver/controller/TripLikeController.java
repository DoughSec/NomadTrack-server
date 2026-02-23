package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.TripLike;
import com.nomadtrack.nomadtrackserver.model.dto.CommentRequest;
import com.nomadtrack.nomadtrackserver.model.dto.TripLikeDto;
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
    public TripLike create(@PathVariable Integer tripId, @RequestBody TripLikeDto request) {
        return tripLikeService.create(
                tripId,
                request.getUserId()
        );
    }

    //get all TripLike records
    @GetMapping("/{tripId}/likes")
    @ResponseStatus(HttpStatus.OK)
    public List<TripLike> getAll(@PathVariable("tripId") Integer tripId) {
        return tripLikeService.getAll(tripId);
    }

    //delete TripLike record
    @DeleteMapping("/likes/{likeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTripLike(@PathVariable("likeId") Integer likeId) {
        tripLikeService.delete(likeId);
    }

}
