package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.dto.CommentRequest;
import com.nomadtrack.nomadtrackserver.model.dto.TripCommentResponseDto;
import com.nomadtrack.nomadtrackserver.security.SecurityUtils;
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
    public TripCommentResponseDto create(@PathVariable Integer tripId,
                                         @RequestBody CommentRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return tripCommentService.create(tripId, currentUserId.intValue(), request.getComment());
    }

    //get all TripComment records
    @GetMapping("/{tripId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<TripCommentResponseDto> getAll(@PathVariable("tripId") Integer tripId) {
        return tripCommentService.getAll(tripId);
    }

    //update TripComment record
    @PutMapping("/{tripId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public TripCommentResponseDto updateTripComment(@PathVariable("tripId") Integer tripId,
                                                     @PathVariable("commentId") Integer commentId,
                                                     @RequestBody CommentRequest request) {
        return tripCommentService.update(tripId, commentId, request.getComment());
    }

    //delete TripComment record
    @DeleteMapping("/{tripId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTripComment(@PathVariable("tripId") Integer tripId,
                                   @PathVariable("commentId") Integer commentId) {
        tripCommentService.delete(tripId, commentId);
    }
}
