package com.nomadtrack.nomadtrackserver.service;

import com.nomadtrack.nomadtrackserver.model.User;
import com.nomadtrack.nomadtrackserver.model.dto.UserProfileDto;
import com.nomadtrack.nomadtrackserver.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // create User
    public User create(
            String firstName, String lastName, String avatarURL, String email, String passwordHash,
            String bio, String address, String role
    ) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAvatarURL(avatarURL);
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

        return userRepository.save(user);
    }

    // getAll
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
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

    // update User
    public User update(Integer userId, UserProfileDto updated) {
        User existing = getById(userId);

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setAvatarURL(updated.getAvatarURL());
        existing.setEmail(updated.getEmail());
        existing.setPasswordHash(updated.getPasswordHash());
        existing.setBio(updated.getBio());
        existing.setAddress(updated.getAddress());

        return userRepository.save(existing);
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
