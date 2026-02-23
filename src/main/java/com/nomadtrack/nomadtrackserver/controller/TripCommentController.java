package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.TripComment;
import com.nomadtrack.nomadtrackserver.model.dto.CommentRequest;
import com.nomadtrack.nomadtrackserver.service.TripCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nomadTrack/trips")
public class TripCommentController {
    private final TripCommentService tripCommentService;

    public TripCommentController(TripCommentService tripCommentService) {
        this.tripCommentService = tripCommentService;
    }

    //create TripComment record
    @PostMapping("/{tripId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public TripComment create(@PathVariable Integer tripId, @RequestBody CommentRequest request) {
        return tripCommentService.create(
                tripId,
                request.getUserId(),
                request.getComment()
        );
    }

    //get all TripComment records
    @GetMapping("/{tripId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<TripComment> getAll(@PathVariable("tripId") Integer tripId) {
        return tripCommentService.getAll(tripId);
    }

    //update TripComment record
    @PutMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public TripComment updateTripComment(@PathVariable("commentId") Integer commentId, @RequestBody CommentRequest request) {
        return tripCommentService.update(commentId, request.getComment());
    }

    //delete TripComment record
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTripComment(@PathVariable("commentId") Integer commentId) {
        tripCommentService.delete(commentId);
    }

}
