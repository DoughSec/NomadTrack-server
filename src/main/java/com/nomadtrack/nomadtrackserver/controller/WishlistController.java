package com.nomadtrack.nomadtrackserver.controller;

import com.nomadtrack.nomadtrackserver.model.Wishlist;
import com.nomadtrack.nomadtrackserver.model.dto.WishlistCompleteRequest;
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
    public Wishlist create(@RequestBody Wishlist request) {
        return wishlistService.create(
                request.getUser().getId(),
                request.getTitle(),
                request.getDescription(),
                request.getTargetCountry(),
                request.getTargetCity(),
                request.getDeadline(),
                request.isCompleted(),
                request.getCompletedDate()
        );
    }

    //get all Wishlist records
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Wishlist> getAll() {
        return wishlistService.getAll();
    }

    //get Wishlist by id
    @GetMapping("/{wishlistId}")
    @ResponseStatus(HttpStatus.OK)
    public Wishlist getWishlistById(@PathVariable("wishlistId") Integer id) {
        return wishlistService.getById(id);
    }

    //mark complete
    @PatchMapping("/{wishlistId}/complete")
    public Wishlist markComplete(@PathVariable("wishlistId") Integer id,
                                 @RequestBody WishlistCompleteRequest request) {
        return wishlistService.markComplete(id, request.isCompleted());
    }

    //update Wishlist record
    @PutMapping("/{wishlistId}")
    @ResponseStatus(HttpStatus.OK)
    public Wishlist updateWishlist(@PathVariable("wishlistId") Integer id, @RequestBody Wishlist wishlist) {
        return wishlistService.update(id, wishlist);
    }

    //delete Wishlist record
    @DeleteMapping("/{wishlistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWishlist(@PathVariable("wishlistId") Integer id) {
        wishlistService.delete(id);
    }

}
