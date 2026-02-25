package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.LoginResponseDto;
import com.nomadtrack.nomadtrackserver.model.dto.UserMeResponse;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.security.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils JwtUtils;

    // 60 minutes
    private static final long TTL_MILLIS = 60L * 60L * 1000L;

    public AuthenticationService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtUtils JwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.JwtUtils = JwtUtils;
    }

    // user login
    public LoginResponseDto login(String email, String rawPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = JwtUtils.createJWT(
                String.valueOf(user.getId()), // token id
                "nomadTrack",                  // issuer
                user.getEmail(),               // subject
                TTL_MILLIS
        );

        return new LoginResponseDto(token, TTL_MILLIS / 1000);
    }

    // get current logged in user
    public UserMeResponse me(String token) {

        Claims claims = JwtUtils.decodeJWT(token);

        // subject = email (from how we created the token)
        String email = claims.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new UserMeResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}