package com.nomadtrack.nomadtrackserver.test;

import com.nomadtrack.nomadtrackserver.exception.BadRequestException;
import com.nomadtrack.nomadtrackserver.exception.ResourceNotFoundException;
import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.LoginResponseDto;
import com.nomadtrack.nomadtrackserver.model.dto.UserMeResponse;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.security.JwtUtils;
import com.nomadtrack.nomadtrackserver.service.AuthenticationService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setEmail("john@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPasswordHash("hashedPassword");
        user.setRole("ROLE_USER");
    }

    @Test
    void login_success() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "hashedPassword")).thenReturn(true);
        when(jwtUtils.createJWT(anyString(), anyString(), anyString(), anyLong(), anyString()))
                .thenReturn("mock-token");

        LoginResponseDto result = authenticationService.login("john@test.com", "rawPassword");

        assertNotNull(result);
        assertEquals("mock-token", result.getAccessToken());
    }

    @Test
    void login_emailNotFound_throws() {
        when(userRepository.findByEmail("bad@test.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> authenticationService.login("bad@test.com", "password"));
    }

    @Test
    void login_wrongPassword_throws() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> authenticationService.login("john@test.com", "wrongPassword"));
    }

    @Test
    void me_success() {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("john@test.com");
        when(jwtUtils.decodeJWT("mock-token")).thenReturn(claims);
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

        UserMeResponse result = authenticationService.me("mock-token");

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("john@test.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void me_userNotFound_throws() {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("missing@test.com");
        when(jwtUtils.decodeJWT("mock-token")).thenReturn(claims);
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authenticationService.me("mock-token"));
    }
}
