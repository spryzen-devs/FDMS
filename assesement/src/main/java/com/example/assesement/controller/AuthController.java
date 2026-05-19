package com.example.assesement.controller;

import com.example.assesement.model.DTOs.*;
import com.example.assesement.model.Role;
import com.example.assesement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("/users/{id}/location")
    public ResponseEntity<UserResponse> updateLocation(
            @PathVariable Long id,
            @RequestParam int x,
            @RequestParam int y) {
        return ResponseEntity.ok(userService.updateLocation(id, x, y));
    }
}
