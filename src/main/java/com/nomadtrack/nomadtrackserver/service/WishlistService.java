package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.Trip;
import com.nomadtrack.nomadtrackserver.model.Wishlist;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.Wishlist;
import com.nomadtrack.nomadtrackserver.model.dto.TripRequestDto;
import com.nomadtrack.nomadtrackserver.model.dto.WishlistRequestDto;
import com.nomadtrack.nomadtrackserver.model.dto.WishlistRequestDto;
import com.nomadtrack.nomadtrackserver.model.dto.WishlistResponseDto;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;

    public WishlistService(
            WishlistRepository wishlistRepository,
            UserRepository userRepository
    ) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
    }

    // create Wishlist
    public WishlistResponseDto create(
            Integer userId, String title, String description, String targetCountry, String targetCity, LocalDate deadline
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setTitle(title);
        wishlist.setDescription(description);
        wishlist.setTargetCountry(targetCountry);
        wishlist.setTargetCity(targetCity);
        wishlist.setDeadline(deadline);
        wishlist.setCompleted(false);
        wishlist.setCompletedDate(null);

        wishlistRepository.save(wishlist);

        WishlistResponseDto responseDto = new WishlistResponseDto();
        responseDto.setWishlistId(wishlist.getId());
        responseDto.setTitle(title);
        responseDto.setDescription(description);
        responseDto.setTargetCountry(targetCountry);
        responseDto.setTargetCity(targetCity);
        responseDto.setDeadline(deadline);

        return responseDto;
    }

    // getAllUserWishlists
    @Transactional(readOnly = true)
    public List<WishlistResponseDto> getAll(Integer userId) {
        List<Wishlist> wishlists = wishlistRepository.findAllByUser_Id(userId);
        List<WishlistResponseDto> wishlistResponseDtos = new ArrayList<>();
        for (Wishlist wishlist : wishlists) {
            WishlistResponseDto wishlistResponseDto = new WishlistResponseDto();
            wishlistResponseDto.setWishlistId(wishlist.getId());
            wishlistResponseDto.setTitle(wishlist.getTitle());
            wishlistResponseDto.setDescription(wishlist.getDescription());
            wishlistResponseDto.setTargetCity(wishlist.getTargetCity());
            wishlistResponseDto.setTargetCountry(wishlist.getTargetCountry());
            wishlistResponseDto.setDeadline(wishlist.getDeadline());
            wishlistResponseDto.setCompleted(wishlist.isCompleted());
            wishlistResponseDto.setCompletedDate(wishlist.getCompletedDate());
            wishlistResponseDtos.add(wishlistResponseDto);
        }
        return wishlistResponseDtos;
    }

    // getByUser
    @Transactional(readOnly = true)
    public List<Wishlist> getByUserId(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        return wishlistRepository.findAllByUser_Id(userId);
    }

    // getById
    @Transactional(readOnly = true)
    public Wishlist getById(Integer wishlistId) {
        if (wishlistId == null) {
            throw new IllegalArgumentException("WishlistId is required");
        }
        return wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found: " + wishlistId));
    }

    // getByCountryName
    @Transactional(readOnly = true)
    public List<WishlistResponseDto> getByTargetCountry(String targetCountry, Integer userId) {
        if (targetCountry == null) {
            throw new IllegalArgumentException("targetCountry is required");
        }
        List<Wishlist> wishlists = wishlistRepository.findByTargetCountryIgnoreCase(targetCountry);

        List<WishlistResponseDto> wishlistResponseDtos = new ArrayList<>();
        for (Wishlist wishlist : wishlists) {
            if (userId.equals(wishlist.getUser().getId())) {
                WishlistResponseDto wishlistResponseDto = new WishlistResponseDto();
                wishlistResponseDto.setWishlistId(wishlist.getId());
                wishlistResponseDto.setTitle(wishlist.getTitle());
                wishlistResponseDto.setTargetCountry(wishlist.getTargetCountry());
                wishlistResponseDto.setTargetCity(wishlist.getTargetCity());
                wishlistResponseDto.setDescription(wishlist.getDescription());
                wishlistResponseDto.setDeadline(wishlist.getDeadline());
                wishlistResponseDto.setCompleted(wishlist.isCompleted());
                wishlistResponseDto.setCompletedDate(wishlist.getCompletedDate());
                wishlistResponseDtos.add(wishlistResponseDto);
            }

        }

        return wishlistResponseDtos;
    }

    // patch update complete
    public WishlistResponseDto markComplete(Integer wishlistId, boolean isCompleted) {
        Wishlist existing = getById(wishlistId);
        existing.setCompleted(isCompleted);

        if (isCompleted) {
            existing.setCompletedDate(LocalDate.now());
        } else {
            existing.setCompletedDate(null);
        }

        wishlistRepository.save(existing);

        WishlistResponseDto responseDto = new WishlistResponseDto();
        responseDto.setWishlistId(existing.getId());
        responseDto.setTitle(existing.getTitle());
        responseDto.setDescription(existing.getDescription());
        responseDto.setTargetCountry(existing.getTargetCountry());
        responseDto.setTargetCity(existing.getTargetCity());
        responseDto.setDeadline(existing.getDeadline());
        responseDto.setCompleted(existing.isCompleted());
        responseDto.setCompletedDate(existing.getCompletedDate());
        return responseDto;
    }


    // update Wishlist
    public WishlistResponseDto update(Integer WishlistId, WishlistRequestDto updated, Integer userId) {
        Wishlist existing = getById(WishlistId);

        if(!existing.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Must be called by the current user");
        }
        existing.setDeadline(updated.getDeadline());
        existing.setDescription(updated.getDescription());
        existing.setTitle(updated.getTitle());
        existing.setTargetCity(updated.getTargetCity());
        existing.setTargetCountry(updated.getTargetCountry());

        wishlistRepository.save(existing);

        WishlistResponseDto responseDto = new WishlistResponseDto();
        responseDto.setWishlistId(existing.getId());
        responseDto.setTitle(existing.getTitle());
        responseDto.setDescription(existing.getDescription());
        responseDto.setTargetCountry(existing.getTargetCountry());
        responseDto.setTargetCity(existing.getTargetCity());
        responseDto.setDeadline(existing.getDeadline());



        return responseDto;
    }

    // delete Wishlist
    public void delete(Integer wishlistId, Integer userId) {
        if (wishlistId == null) {
            throw new IllegalArgumentException("WishlistId is required");
        }
        if (!wishlistRepository.existsById(wishlistId)) {
            throw new IllegalArgumentException("Wishlist not found: " + wishlistId);
        }

        Wishlist wishlist = getById(wishlistId);
        if (!wishlist.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Wishlist user not found: " + wishlistId);
        }

        wishlistRepository.deleteById(wishlistId);
    }
}
