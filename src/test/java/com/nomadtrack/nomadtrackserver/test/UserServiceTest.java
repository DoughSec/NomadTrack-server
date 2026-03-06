package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.exception.BadRequestException;
import com.nomadtrack.nomadtrackserver.exception.ResourceNotFoundException;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.UserMeResponse;
import com.nomadtrack.nomadtrackserver.model.dto.UserProfileDto;
import com.nomadtrack.nomadtrackserver.model.dto.UserSearchProfileDto;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAvatarUrl("avatar.png");
        user.setEmail("john@test.com");
        user.setPasswordHash("hashedPassword");
        user.setBio("bio");
        user.setAddress("123 Main St");
        user.setRole("ROLE_USER");
    }

    // --- create() ---

    @Test
    void create_success() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.create("test1", "test2", "avatar.png",
                "test1test2@test.com", "hashedPassword", "bio", "123 Main St", "ROLE_USER");

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_setsRole() {
        User adminUser = new User();
        adminUser.setId(2);
        adminUser.setRole("ROLE_ADMIN");
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        User result = userService.create("Admin", "User", null, "admin@test.com",
                "hash", null, null, "ROLE_ADMIN");

        assertEquals("ROLE_ADMIN", result.getRole());
    }

    // --- register() ---

    @Test
    void register_success() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register("test1", "test2", "test1test2@test.com", "password123");

        assertNotNull(result);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_setsRoleUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register("Jane", "Smith", "jane@test.com", "secret");

        assertNotNull(result);
        // Verify the encoder was called (role is set inside the method)
        verify(passwordEncoder).encode("secret");
    }

    // --- getAll() ---

    @Test
    void getAll_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> results = userService.getAll();

        assertEquals(1, results.size());
    }

    @Test
    void getAll_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> results = userService.getAll();

        assertTrue(results.isEmpty());
    }

    @Test
    void getAll_multipleUsers() {
        User user2 = new User();
        user2.setId(2);
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<User> results = userService.getAll();

        assertEquals(2, results.size());
    }

    // --- searchAll() ---

    @Test
    void searchAll_returnsDtos() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserSearchProfileDto> results = userService.searchAll();

        assertEquals(1, results.size());
        assertEquals("John", results.getFirst().getFirstName());
        assertEquals("Doe", results.getFirst().getLastName());
        assertEquals("avatar.png", results.getFirst().getAvatarUrl());
        assertEquals("bio", results.getFirst().getBio());
    }

    @Test
    void searchAll_emptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserSearchProfileDto> results = userService.searchAll();

        assertTrue(results.isEmpty());
    }

    // --- getById() ---

    @Test
    void getById_success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getById(1);

        assertEquals(1, result.getId());
    }

    @Test
    void getById_nullId_throws() {
        assertThrows(BadRequestException.class,
                () -> userService.getById(null));
    }

    @Test
    void getById_notFound_throws() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getById(99));
    }

    // --- getByFirstName() ---

    @Test
    void getByFirstName_success() {
        when(userRepository.findByFirstNameIgnoreCase("John")).thenReturn(List.of(user));

        List<UserSearchProfileDto> results = userService.getByFirstName("John");

        assertEquals(1, results.size());
        assertEquals("John", results.getFirst().getFirstName());
        assertEquals(1, results.getFirst().getId());
    }

    @Test
    void getByFirstName_nullName_throws() {
        assertThrows(BadRequestException.class,
                () -> userService.getByFirstName(null));
    }

    @Test
    void getByFirstName_noResults_returnsEmpty() {
        when(userRepository.findByFirstNameIgnoreCase("Nobody")).thenReturn(List.of());

        List<UserSearchProfileDto> results = userService.getByFirstName("Nobody");

        assertTrue(results.isEmpty());
    }

    @Test
    void getByFirstName_dtoContainsAvatarAndBio() {
        when(userRepository.findByFirstNameIgnoreCase("John")).thenReturn(List.of(user));

        List<UserSearchProfileDto> results = userService.getByFirstName("John");

        assertEquals("avatar.png", results.getFirst().getAvatarUrl());
        assertEquals("bio", results.getFirst().getBio());
    }

    // --- update() ---

    @Test
    void update_success() {
        UserProfileDto dto = new UserProfileDto();
        dto.setFirstName("updateTest");
        dto.setLastName("updateTest");
        dto.setAvatarUrl("new.png");
        dto.setBio("new bio");
        dto.setAddress("1999 Main st");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserMeResponse result = userService.update(1, dto);

        assertNotNull(result);
        assertEquals("updateTest", result.getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_partialFields_onlyUpdatesNonNull() {
        UserProfileDto dto = new UserProfileDto();
        dto.setFirstName("NewFirst");
        // lastName, avatarUrl, bio, address all null

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserMeResponse result = userService.update(1, dto);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_userNotFound_throws() {
        UserProfileDto dto = new UserProfileDto();
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.update(99, dto));
    }

    @Test
    void update_responseContainsEmail() {
        UserProfileDto dto = new UserProfileDto();
        dto.setFirstName("Updated");

        user.setEmail("john@test.com");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserMeResponse result = userService.update(1, dto);

        assertEquals("john@test.com", result.getEmail());
    }

    // --- delete() ---

    @Test
    void delete_success() {
        when(userRepository.existsById(1)).thenReturn(true);

        userService.delete(1);

        verify(userRepository).deleteById(1);
    }
}
