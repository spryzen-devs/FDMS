package com.example.assesement.controller;

import com.example.assesement.model.DTOs.*;
import com.example.assesement.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@RequestBody RestaurantRequest request) {
        return ResponseEntity.ok(restaurantService.createRestaurant(request));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @PostMapping("/menu")
    public ResponseEntity<FoodItemResponse> addFoodItem(@RequestBody FoodItemRequest request) {
        return ResponseEntity.ok(restaurantService.addFoodItem(request));
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<List<FoodItemResponse>> getMenu(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getMenu(id));
    }
}
