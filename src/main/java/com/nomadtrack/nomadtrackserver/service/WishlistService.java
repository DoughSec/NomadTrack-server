package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.Wishlist;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    // create Wishlist --- TODO: userId should just be the principal
    public Wishlist create(
            Integer userId, String title, String description, String targetCountry, String targetCity, LocalDate deadline,
            boolean isCompleted, LocalDate completedDAte
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
        wishlist.setCompleted(isCompleted);
        wishlist.setDeadline(deadline);
        wishlist.setCompletedDate(completedDAte);

        return wishlistRepository.save(wishlist);
    }

    // getAll
    @Transactional(readOnly = true)
    public List<Wishlist> getAll() {
        return wishlistRepository.findAll();
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

    // update Wishlist
    public Wishlist update(Integer WishlistId, Wishlist updated) {
        Wishlist existing = getById(WishlistId);

        existing.setCompleted(updated.isCompleted());
        existing.setCompletedDate(updated.getCompletedDate());
        existing.setDeadline(updated.getDeadline());
        existing.setDescription(updated.getDescription());
        existing.setTitle(updated.getTitle());
        existing.setTargetCity(updated.getTargetCity());
        existing.setTargetCountry(updated.getTargetCountry());

        return wishlistRepository.save(existing);
    }

    // delete Wishlist
    public void delete(Integer wishlistId) {
        if (wishlistId == null) {
            throw new IllegalArgumentException("WishlistId is required");
        }
        if (!wishlistRepository.existsById(wishlistId)) {
            throw new IllegalArgumentException("Wishlist not found: " + wishlistId);
        }
        wishlistRepository.deleteById(wishlistId);
    }
}
