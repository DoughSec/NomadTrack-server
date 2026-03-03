package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.UserMeResponse;
import com.nomadtrack.nomadtrackserver.model.dto.UserProfileDto;
import com.nomadtrack.nomadtrackserver.model.dto.UserSearchProfileDto;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import com.nomadtrack.nomadtrackserver.security.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // create User
    public User create(
            String firstName, String lastName, String avatarUrl, String email, String passwordHash,
            String bio, String address, String role
    ) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAvatarUrl(avatarUrl);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setBio(bio);
        user.setAddress(address);
        user.setRole(role);

        return userRepository.save(user);
    }

    // registration for users
    public User register(
            String firstName, String lastName, String email, String password
    ) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(this.passwordEncoder.encode(password));
        user.setRole("ROLE_USER");

        return userRepository.save(user);
    }

    // getAll
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // searchAll user profiles
    @Transactional(readOnly = true)
    public List<UserSearchProfileDto> searchAll() {
        List<User> users = userRepository.findAll();
        List<UserSearchProfileDto> userSearchProfileDtos = new ArrayList<>();
        for (User user : users) {
            UserSearchProfileDto userSearchProfileDto = new UserSearchProfileDto();
            userSearchProfileDto.setFirstName(user.getFirstName());
            userSearchProfileDto.setLastName(user.getLastName());
            userSearchProfileDto.setAvatarUrl(user.getAvatarUrl());
            userSearchProfileDto.setBio(user.getBio());
            userSearchProfileDtos.add(userSearchProfileDto);
        }
        return userSearchProfileDtos;
    }

    // getById
    @Transactional(readOnly = true)
    public User getById(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId is required");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    // getByFirstName
    @Transactional(readOnly = true)
    public List<UserSearchProfileDto> getByFirstName(String firstName) {
        List<User> users = userRepository.findByFirstNameIgnoreCase(firstName);
        List<UserSearchProfileDto> userSearchProfileDtos = new ArrayList<>();
        for (User user : users) {
            UserSearchProfileDto userSearchProfileDto = new UserSearchProfileDto();
            userSearchProfileDto.setId(user.getId());
            userSearchProfileDto.setFirstName(user.getFirstName());
            userSearchProfileDto.setLastName(user.getLastName());
            userSearchProfileDto.setAvatarUrl(user.getAvatarUrl());
            userSearchProfileDto.setBio(user.getBio());
            userSearchProfileDtos.add(userSearchProfileDto);
        }
        if (firstName == null) {
            throw new IllegalArgumentException("No user exists with name: " + firstName);
        }
        return userSearchProfileDtos;
    }


    // update logged in user
    public UserMeResponse update(Integer userId, UserProfileDto dto) {

        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Only update fields that are allowed to change
        if (dto.getFirstName() != null) {
            existing.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            existing.setLastName(dto.getLastName());
        }

        if (dto.getBio() != null) {
            existing.setBio(dto.getBio());
        }

        if (dto.getAvatarUrl() != null) {
            existing.setAvatarUrl(dto.getAvatarUrl());
        }

        if (dto.getAddress() != null) {
            existing.setAddress(dto.getAddress());
        }

        User saved = userRepository.save(existing);

        UserMeResponse userMeResponse = new UserMeResponse();
        userMeResponse.setId(saved.getId());
        userMeResponse.setEmail(saved.getEmail());
        userMeResponse.setFirstName(saved.getFirstName());
        userMeResponse.setLastName(saved.getLastName());
        userMeResponse.setBio(saved.getBio());
        userMeResponse.setAddress(saved.getAddress());
        userMeResponse.setAvatarUrl(saved.getAvatarUrl());

        return userMeResponse;
    }

    // delete User
    public void delete(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId is required");
        }
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        userRepository.deleteById(userId);
    }
}
