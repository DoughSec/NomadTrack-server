package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.UserProfileDto;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/nomadTrack/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    //get all User records / search users
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAll() {
        return userService.getAll();
    }

    //get current user profile
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(Principal principal) {
        return userService.getById(getCurrentUserId(principal));
    }

    //get Trip by id
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User getTripById(@PathVariable("userId") Integer userId) {
        return userService.getById(userId);
    }

    //update User record
    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@PathVariable("userId") Integer userId, @RequestBody UserProfileDto user) {
        return userService.update(userId, user);
    }

    private Integer getCurrentUserId(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new IllegalArgumentException("User is not authenticated");
        }

        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        return currentUser.getId();
    }

}
