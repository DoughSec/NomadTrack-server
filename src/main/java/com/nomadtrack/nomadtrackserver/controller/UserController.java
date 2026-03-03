package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.UserMeResponse;
import com.nomadtrack.nomadtrackserver.model.dto.UserProfileDto;
import com.nomadtrack.nomadtrackserver.model.dto.UserSearchProfileDto;
import com.nomadtrack.nomadtrackserver.service.AuthenticationService;
import com.nomadtrack.nomadtrackserver.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nomadTrack/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    //get all User records(admin)
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> getAllAsAdmin() {
        return userService.getAll();
    }

    //get all User records / search users
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<UserSearchProfileDto> searchAll() {
        return userService.searchAll();
    }

    //get user by id
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User getByUserId(@PathVariable("userId") Integer userId) {
        return userService.getById(userId);
    }

    //get user by id
    @GetMapping("/search/{firstName}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserSearchProfileDto> getByFirstName(@PathVariable("firstName") String firstName) {
        return userService.getByFirstName(firstName);
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserMeResponse updateMe(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserProfileDto dto
    ) {
        String token = authenticationService.extractBearerToken(authorizationHeader);
        UserMeResponse userMeResponse = authenticationService.me(token);
        return userService.update(userMeResponse.getId(), dto);
    }

}
