package com.example.assesement.service;

import com.example.assesement.entity.User;
import com.example.assesement.model.DTOs.*;
import com.example.assesement.model.Role;
import com.example.assesement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GridSimulationService gridSimulationService;

    @Transactional
    public UserResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + req.getEmail());
        }
        
        int x = req.getX() != null ? req.getX() : 0;
        int y = req.getY() != null ? req.getY() : 0;
        
        if (!gridSimulationService.isValidLocation(x, y)) {
            throw new RuntimeException("Invalid location coordinates. Must be between 0 and 100.");
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(req.getPassword())
                .phone(req.getPhone())
                .role(req.getRole())
                .x(x)
                .y(y)
                .zone(req.getZone() != null ? req.getZone() : "Downtown")
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        return convertToResponse(saved);
    }

    public UserResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(req.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return convertToResponse(user);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateLocation(Long id, int x, int y) {
        if (!gridSimulationService.isValidLocation(x, y)) {
            throw new RuntimeException("Location out of grid bounds (0-100)");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setX(x);
        user.setY(y);
        return convertToResponse(userRepository.save(user));
    }

    public UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .isActive(user.getIsActive())
                .x(user.getX())
                .y(user.getY())
                .zone(user.getZone())
                .build();
    }
}
