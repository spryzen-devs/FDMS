package com.example.assesement.repository;

import com.example.assesement.entity.FoodItem;
import com.example.assesement.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByRestaurant(Restaurant restaurant);
    List<FoodItem> findByRestaurantAndIsAvailableTrue(Restaurant restaurant);
}
