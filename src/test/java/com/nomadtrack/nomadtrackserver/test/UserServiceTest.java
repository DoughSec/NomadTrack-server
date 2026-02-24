package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.UserProfileDto;
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
        user.setAvatarURL("avatar.png");
        user.setEmail("john@test.com");
        user.setPasswordHash("hashedPassword");
        user.setBio("bio");
        user.setAddress("123 Main St");
        user.setRole("USER");
    }

    @Test
    void create_success() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.create("test1", "test2", "avatar.png",
                "test1test2@test.com", "hashedPassword", "bio", "123 Main St", "USER");

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(userRepository).save(any(User.class));
    }

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
    void getAll_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> results = userService.getAll();

        assertEquals(1, results.size());
    }

    @Test
    void getById_success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getById(1);

        assertEquals(1, result.getId());
    }

    @Test
    void getById_nullId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.getById(null));
    }

    @Test
    void getById_notFound_throws() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.getById(99));
    }

    @Test
    void update_success() {
        UserProfileDto dto = new UserProfileDto();
        dto.setFirstName("updateTest");
        dto.setLastName("updateTest");
        dto.setAvatarURL("new.png");
        dto.setEmail("update@test.com");
        dto.setPasswordHash("newHash");
        dto.setBio("new bio");
        dto.setAddress("1999 Main st");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.update(1, dto);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void delete_success() {
        when(userRepository.existsById(1)).thenReturn(true);

        userService.delete(1);

        verify(userRepository).deleteById(1);
    }

    @Test
    void delete_nullId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.delete(null));
    }

    @Test
    void delete_notFound_throws() {
        when(userRepository.existsById(99)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.delete(99));
    }
}
