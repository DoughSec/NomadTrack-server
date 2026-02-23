package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.dto.MapPinDto;
import com.nomadtrack.nomadtrackserver.service.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nomadTrack/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    //create Trip record
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Trip create(@RequestBody Trip request) {
        return tripService.create(
                request.getUser().getId(),
                request.getTitle(),
                request.getCity(),
                request.getCountry(),
                request.getStartDate(),
                request.getEndDate(),
                request.getNotes(),
                request.getLatitude(),
                request.getLongitude(),
                request.getVisibility()
                );
    }

    //get all Trip records
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Trip> getAll() {
        return tripService.getAll();
    }

    //get Trip by id
    @GetMapping("/{tripId}")
    @ResponseStatus(HttpStatus.OK)
    public Trip getTripById(@PathVariable("tripId") Integer id) {
        return tripService.getById(id);
    }

    //get map locations/pins
    @GetMapping("/map/locations")
    @ResponseStatus(HttpStatus.OK)
    public List<MapPinDto> getMapLocations() {
        return tripService.getMapPins();
    }

    //update Trip record
    @PutMapping("/{tripId}")
    @ResponseStatus(HttpStatus.OK)
    public Trip updateTrip(@PathVariable("tripId") Integer id, @RequestBody Trip trip) {
        return tripService.update(id, trip);
    }

    //delete Trip record
    @DeleteMapping("/{tripId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrip(@PathVariable("id") Integer id) {
        tripService.delete(id);
    }

}
