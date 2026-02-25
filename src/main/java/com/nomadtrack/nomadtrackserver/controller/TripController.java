package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.dto.MapPinDto;
import com.nomadtrack.nomadtrackserver.model.dto.TripRequestDto;
import com.nomadtrack.nomadtrackserver.model.dto.UserMeResponse;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.service.AuthenticationService;
import com.nomadtrack.nomadtrackserver.service.TripService;
import com.nomadtrack.nomadtrackserver.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nomadTrack/trips")
public class TripController {
    private final TripService tripService;
    private final AuthenticationService authService;

    public TripController(TripService tripService, AuthenticationService authService) {
        this.tripService = tripService;
        this.authService = authService;
    }

    //create Trip record
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripRequestDto createTrip(@RequestBody TripRequestDto request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return tripService.create(
                currentUserId.intValue(),
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
    public List<TripRequestDto> getAll() {
        return tripService.getAll();
    }

    //get Trip by countryName
    @GetMapping("/{countryName}")
    @ResponseStatus(HttpStatus.OK)
    public List<TripRequestDto> getTripById(@PathVariable("countryName") String countryName) {

        return tripService.getByCountryName(countryName);
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
    public TripRequestDto updateTrip(@PathVariable("tripId") Integer tripId, @RequestHeader("Authorization") String authorizationHeader,
                           @RequestBody TripRequestDto dto) {
        String token = authService.extractBearerToken(authorizationHeader);
        UserMeResponse userMeResponse = authService.me(token);
        return tripService.update(userMeResponse.getId(), dto);
    }

    //delete Trip record
    @DeleteMapping("/{tripId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrip(@PathVariable("tripId") Integer tripId, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authService.extractBearerToken(authorizationHeader);
        UserMeResponse userMeResponse = authService.me(token);

        Trip tripToDelete = tripService.getById(tripId);

        if(userMeResponse.getId().equals(tripToDelete.getUser().getId())) {
            tripService.delete(tripId);
        } else {
            throw new IllegalArgumentException("Trip does not belong to the user");
        }
    }

}
