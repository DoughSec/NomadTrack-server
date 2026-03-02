package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.dto.*;
import com.nomadtrack.nomadtrackserver.security.SecurityUtils;
import com.nomadtrack.nomadtrackserver.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nomadTrack/wishlists")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    //create Wishlist record
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistResponseDto create(@RequestBody WishlistRequestDto request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return wishlistService.create(
                currentUserId.intValue(),
                request.getTitle(),
                request.getDescription(),
                request.getTargetCountry(),
                request.getTargetCity(),
                request.getDeadline()
        );
    }

    //get all user Wishlist records
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WishlistResponseDto> getAll() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return wishlistService.getAll(currentUserId.intValue());
    }

    //get Wishlist by targetCountry
    @GetMapping("/{targetCountry}")
    @ResponseStatus(HttpStatus.OK)
    public List<WishlistResponseDto> getWishlistById(@PathVariable("targetCountry") String targetCountry) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        return wishlistService.getByTargetCountry(targetCountry, currentUserId.intValue());
    }

    //mark complete
    @PatchMapping("/{wishlistId}/complete")
    public WishlistResponseDto markComplete(@PathVariable("wishlistId") Integer id,
                                            @RequestBody WishlistCompleteRequest request) {
        return wishlistService.markComplete(id, request.isCompleted());
    }

    //update Wishlist record
    @PutMapping("/{wishlistId}")
    @ResponseStatus(HttpStatus.OK)
    public WishlistResponseDto updateWishlist(@PathVariable("wishlistId") Integer id, @RequestBody WishlistRequestDto request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        return wishlistService.update(id, request, currentUserId.intValue());
    }

    //delete Wishlist record
    @DeleteMapping("/{wishlistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWishlist(@PathVariable("wishlistId") Integer id,
                               @RequestHeader("Authorization") String authorizationHeader) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        wishlistService.delete(id, currentUserId.intValue());
    }

}
