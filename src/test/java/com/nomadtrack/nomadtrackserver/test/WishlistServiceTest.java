package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.exception.BadRequestException;
import com.nomadtrack.nomadtrackserver.exception.ResourceNotFoundException;
import com.nomadtrack.nomadtrackserver.model.Wishlist;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.WishlistRequestDto;
import com.nomadtrack.nomadtrackserver.model.dto.WishlistResponseDto;
import com.nomadtrack.nomadtrackserver.repository.WishlistRepository;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistServiceTest {
    @Mock
    private WishlistRepository wishlistRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WishlistService wishlistService;

    private Wishlist wishlist;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        wishlist = new Wishlist();
        wishlist.setId(1);
        wishlist.setUser(user);
        wishlist.setTitle("testTitle");
        wishlist.setDescription("testDescription");
        wishlist.setTargetCountry("targetCountry");
        wishlist.setTargetCity("targetCity");
        wishlist.setDeadline(LocalDate.of(2027, 1, 1));
        wishlist.setCompleted(false);
        wishlist.setCompletedDate(null);
    }

    @Test
    void create_success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        WishlistResponseDto result = wishlistService.create(1, "testTitle", "testDescription",
                "targetCountry", "targetCity", LocalDate.of(2027, 1, 1));

        assertNotNull(result);
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void create_nullUserId_throws() {
        assertThrows(BadRequestException.class,
                () -> wishlistService.create(null, "testTitle", "testDescription",
                        "targetCountry", "targetCity", LocalDate.of(2027, 1, 1)));
    }

    @Test
    void create_userNotFound_throws() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> wishlistService.create(99, "testTitle", "testDescription",
                        "targetCountry", "targetCity", LocalDate.of(2027, 1, 1)));
    }

    @Test
    void getByUserId_success() {
        when(wishlistRepository.findAllByUser_Id(1)).thenReturn(List.of(wishlist));

        List<Wishlist> results = wishlistService.getByUserId(1);

        assertEquals(1, results.size());
    }

    @Test
    void getByUserId_nullId_throws() {
        assertThrows(BadRequestException.class,
                () -> wishlistService.getByUserId(null));
    }

    @Test
    void getById_success() {
        when(wishlistRepository.findById(1)).thenReturn(Optional.of(wishlist));

        Wishlist result = wishlistService.getById(1);

        assertEquals(1, result.getId());
    }

    @Test
    void getById_nullId_throws() {
        assertThrows(BadRequestException.class,
                () -> wishlistService.getById(null));
    }

    @Test
    void markComplete_true() {
        wishlist.setCompleted(false);
        when(wishlistRepository.findById(1)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        WishlistResponseDto result = wishlistService.markComplete(1, true);

        assertTrue(result.isCompleted());
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void markComplete_false() {
        wishlist.setCompleted(true);
        wishlist.setCompletedDate(LocalDate.now());
        when(wishlistRepository.findById(1)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        WishlistResponseDto result = wishlistService.markComplete(1, false);

        assertFalse(result.isCompleted());
        assertNull(result.getCompletedDate());
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void update_success() {
        WishlistRequestDto dto = new WishlistRequestDto();
        dto.setTitle("testTitle");
        dto.setDescription("testDescription");
        dto.setTargetCountry("targetCountry");
        dto.setTargetCity("targetCity");
        dto.setDeadline(LocalDate.of(2027, 1, 1));

        when(wishlistRepository.findById(1)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        WishlistResponseDto result = wishlistService.update(1, dto, 1);

        assertEquals("testTitle", result.getTitle());
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void delete_success() {
        when(wishlistRepository.existsById(1)).thenReturn(true);
        when(wishlistRepository.findById(1)).thenReturn(Optional.of(wishlist));

        wishlistService.delete(1, 1);

        verify(wishlistRepository).deleteById(1);
    }

    @Test
    void delete_nullId_throws() {
        assertThrows(BadRequestException.class,
                () -> wishlistService.delete(null, 1));
    }

    @Test
    void delete_notFound_throws() {
        when(wishlistRepository.existsById(99)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> wishlistService.delete(99, 1));
    }
}
