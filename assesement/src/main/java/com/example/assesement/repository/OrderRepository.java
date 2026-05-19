package com.example.assesement.repository;

import com.example.assesement.entity.Order;
import com.example.assesement.entity.User;
import com.example.assesement.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(User customer);
    List<Order> findByRestaurant(Restaurant restaurant);
}
