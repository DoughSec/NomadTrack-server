package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.exception.BadRequestException;
import com.nomadtrack.nomadtrackserver.exception.ForbiddenException;
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

    // --- create() ---

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
    void create_dtoContainsCorrectFields() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        WishlistResponseDto result = wishlistService.create(1, "testTitle", "testDescription",
                "targetCountry", "targetCity", LocalDate.of(2027, 1, 1));

        assertEquals("testTitle", result.getTitle());
        assertEquals("testDescription", result.getDescription());
        assertEquals("targetCountry", result.getTargetCountry());
        assertEquals("targetCity", result.getTargetCity());
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

    // --- getAll() ---

    @Test
    void getAll_returnsDtoList() {
        when(wishlistRepository.findAllByUser_Id(1)).thenReturn(List.of(wishlist));

        List<WishlistResponseDto> results = wishlistService.getAll(1);

        assertEquals(1, results.size());
        assertEquals("testTitle", results.getFirst().getTitle());
    }

    @Test
    void getAll_returnsEmptyList() {
        when(wishlistRepository.findAllByUser_Id(1)).thenReturn(List.of());

        List<WishlistResponseDto> results = wishlistService.getAll(1);

        assertTrue(results.isEmpty());
    }

    @Test
    void getAll_dtoContainsAllFields() {
        when(wishlistRepository.findAllByUser_Id(1)).thenReturn(List.of(wishlist));

        List<WishlistResponseDto> results = wishlistService.getAll(1);

        WishlistResponseDto dto = results.getFirst();
        assertEquals(1, dto.getWishlistId());
        assertEquals("testTitle", dto.getTitle());
        assertEquals("testDescription", dto.getDescription());
        assertEquals("targetCity", dto.getTargetCity());
        assertEquals("targetCountry", dto.getTargetCountry());
        assertFalse(dto.isCompleted());
    }

    // --- getByUserId() ---

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

    // --- getById() ---

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
    void getById_notFound_throws() {
        when(wishlistRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> wishlistService.getById(99));
    }

    // --- getByTargetCountry() ---

    @Test
    void getByTargetCountry_success() {
        when(wishlistRepository.findByTargetCountryIgnoreCase("targetCountry"))
                .thenReturn(List.of(wishlist));

        List<WishlistResponseDto> results = wishlistService.getByTargetCountry("targetCountry", 1);

        assertEquals(1, results.size());
        assertEquals("targetCountry", results.getFirst().getTargetCountry());
    }

    @Test
    void getByTargetCountry_nullCountry_throws() {
        assertThrows(BadRequestException.class,
                () -> wishlistService.getByTargetCountry(null, 1));
    }

    @Test
    void getByTargetCountry_filtersOtherUsers() {
        User otherUser = new User();
        otherUser.setId(2);
        Wishlist otherWishlist = new Wishlist();
        otherWishlist.setId(2);
        otherWishlist.setUser(otherUser);
        otherWishlist.setTargetCountry("targetCountry");

        when(wishlistRepository.findByTargetCountryIgnoreCase("targetCountry"))
                .thenReturn(List.of(wishlist, otherWishlist));

        List<WishlistResponseDto> results = wishlistService.getByTargetCountry("targetCountry", 1);

        assertEquals(1, results.size());
    }

    @Test
    void getByTargetCountry_noMatch_returnsEmpty() {
        when(wishlistRepository.findByTargetCountryIgnoreCase("Nowhere"))
                .thenReturn(List.of());

        List<WishlistResponseDto> results = wishlistService.getByTargetCountry("Nowhere", 1);

        assertTrue(results.isEmpty());
    }

    // --- markComplete() ---

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
    void markComplete_true_setsCompletedDate() {
        wishlist.setCompleted(false);
        when(wishlistRepository.findById(1)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        wishlistService.markComplete(1, true);

        assertNotNull(wishlist.getCompletedDate());
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
    void markComplete_false_clearsCompletedDate() {
        wishlist.setCompleted(true);
        wishlist.setCompletedDate(LocalDate.of(2025, 1, 1));
        when(wishlistRepository.findById(1)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        wishlistService.markComplete(1, false);

        assertNull(wishlist.getCompletedDate());
    }

    // --- update() ---

    @Test
    void update_success() {
        WishlistRequestDto dto = new WishlistRequestDto();
        dto.setTitle("updatedTitle");
        dto.setDescription("updatedDescription");
        dto.setTargetCountry("updatedCountry");
        dto.setTargetCity("updatedCity");
        dto.setDeadline(LocalDate.of(2028, 6, 15));

        when(wishlistRepository.findById(1)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        WishlistResponseDto result = wishlistService.update(1, dto, 1);

        assertEquals("updatedTitle", result.getTitle());
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void update_wrongOwner_throws() {
        WishlistRequestDto dto = new WishlistRequestDto();
        dto.setTitle("x");
        when(wishlistRepository.findById(1)).thenReturn(Optional.of(wishlist));

        assertThrows(ForbiddenException.class,
                () -> wishlistService.update(1, dto, 99));
    }

    @Test
    void update_wishlistNotFound_throws() {
        WishlistRequestDto dto = new WishlistRequestDto();
        when(wishlistRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> wishlistService.update(99, dto, 1));
    }

    // --- delete() ---

    @Test
    void delete_success() {
        when(wishlistRepository.existsById(1)).thenReturn(true);
        when(wishlistRepository.findById(1)).thenReturn(Optional.of(wishlist));

        wishlistService.delete(1, 1);

        verify(wishlistRepository).deleteById(1);
    }

    @Test
    void delete_wrongOwner_throws() {
        when(wishlistRepository.existsById(1)).thenReturn(true);
        when(wishlistRepository.findById(1)).thenReturn(Optional.of(wishlist));

        assertThrows(ForbiddenException.class,
                () -> wishlistService.delete(1, 99));
    }
}
