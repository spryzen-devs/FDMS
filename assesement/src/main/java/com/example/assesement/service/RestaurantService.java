package com.example.assesement.service;

import com.example.assesement.entity.FoodItem;
import com.example.assesement.entity.Restaurant;
import com.example.assesement.entity.User;
import com.example.assesement.model.DTOs.*;
import com.example.assesement.model.Role;
import com.example.assesement.repository.FoodItemRepository;
import com.example.assesement.repository.RestaurantRepository;
import com.example.assesement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private GridSimulationService gridSimulationService;

    @Transactional
    public RestaurantResponse createRestaurant(RestaurantRequest req) {
        User manager = userRepository.findById(req.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        if (manager.getRole() != Role.RESTAURANT_MANAGER) {
            throw new RuntimeException("Specified user is not a restaurant manager");
        }

        int x = req.getX() != null ? req.getX() : 0;
        int y = req.getY() != null ? req.getY() : 0;

        if (!gridSimulationService.isValidLocation(x, y)) {
            throw new RuntimeException("Invalid location. Must be between 0 and 100.");
        }

        Restaurant restaurant = Restaurant.builder()
                .name(req.getName())
                .location(req.getLocation())
                .averagePreparationTime(req.getAveragePreparationTime() != null ? req.getAveragePreparationTime() : 15)
                .rating(req.getRating() != null ? req.getRating() : 5.0)
                .manager(manager)
                .x(x)
                .y(y)
                .delayCount(0)
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);
        return convertToResponse(saved);
    }

    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return convertToResponse(restaurant);
    }

    @Transactional
    public FoodItemResponse addFoodItem(FoodItemRequest req) {
        Restaurant restaurant = restaurantRepository.findById(req.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        FoodItem item = FoodItem.builder()
                .name(req.getName())
                .price(req.getPrice())
                .category(req.getCategory())
                .restaurant(restaurant)
                .isAvailable(true)
                .build();

        FoodItem saved = foodItemRepository.save(item);
        return convertFoodItemToResponse(saved);
    }

    public List<FoodItemResponse> getMenu(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return foodItemRepository.findByRestaurant(restaurant).stream()
                .map(this::convertFoodItemToResponse)
                .collect(Collectors.toList());
    }

    public RestaurantResponse convertToResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .location(restaurant.getLocation())
                .averagePreparationTime(restaurant.getAveragePreparationTime())
                .rating(restaurant.getRating())
                .managerId(restaurant.getManager().getId())
                .x(restaurant.getX())
                .y(restaurant.getY())
                .delayCount(restaurant.getDelayCount())
                .build();
    }

    public FoodItemResponse convertFoodItemToResponse(FoodItem item) {
        return FoodItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .category(item.getCategory())
                .isAvailable(item.getIsAvailable())
                .restaurantId(item.getRestaurant().getId())
                .build();
    }
}
