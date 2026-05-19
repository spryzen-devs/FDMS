package com.example.assesement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.assesement.entity.*;
import com.example.assesement.model.*;
import com.example.assesement.model.DTOs.*;
import com.example.assesement.repository.*;
import com.example.assesement.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryBusinessRulesTests {

    @InjectMocks
    private DeliveryAssignmentService deliveryAssignmentService;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private DeliveryAssignmentRepository deliveryAssignmentRepository;

    @Mock
    private DeliveryPerformanceRepository deliveryPerformanceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private GridSimulationService gridSimulationService;

    @Mock
    private OrderService mockOrderService;

    private User agent;
    private User customer;
    private Restaurant restaurant;
    private Order order;

    @BeforeEach
    void setUp() {
        agent = User.builder()
                .id(1L)
                .name("Speedy John")
                .role(Role.DELIVERY_AGENT)
                .x(10)
                .y(10)
                .zone("Downtown")
                .isActive(true)
                .build();

        customer = User.builder()
                .id(2L)
                .name("Alice Smith")
                .role(Role.CUSTOMER)
                .x(30)
                .y(30)
                .isActive(true)
                .build();

        restaurant = Restaurant.builder()
                .id(10L)
                .name("Burger Hub")
                .location("Downtown")
                .averagePreparationTime(15)
                .x(15)
                .y(15)
                .build();

        order = Order.builder()
                .id(100L)
                .customer(customer)
                .restaurant(restaurant)
                .orderStatus(OrderStatus.PLACED)
                .totalAmount(BigDecimal.valueOf(25.50))
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(30))
                .build();
    }

    @Test
    void testOneAgentOneActiveDeliveryConstraint() {
        // Mock that the agent already has an active assignment
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(userRepository.findById(1L)).thenReturn(Optional.of(agent));
        when(deliveryAssignmentRepository.existsByDeliveryAgentIdAndDeliveryStatusIn(eq(1L), anyList())).thenReturn(true);

        // Execute & Assert: Should throw an exception because the agent is already busy
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deliveryAssignmentService.assignAgent(100L, 1L);
        });

        assertEquals("Agent already handling active order", exception.getMessage());
    }

    @Test
    void testCannotReassignAfterPickup() {
        // Mock that the order is already assigned and picked up
        DeliveryAssignment existingAssignment = DeliveryAssignment.builder()
                .order(order)
                .deliveryAgent(agent)
                .deliveryStatus(DeliveryStatus.PICKED_UP)
                .build();

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(userRepository.findById(1L)).thenReturn(Optional.of(agent));
        when(deliveryAssignmentRepository.findByOrder(order)).thenReturn(Optional.of(existingAssignment));

        // Execute & Assert: Reassignment should fail because order is already picked up
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deliveryAssignmentService.assignAgent(100L, 1L);
        });

        assertEquals("Cannot reassign after pickup", exception.getMessage());
    }

    @Test
    void testLateDeliveryPenalty() {
        // Mock delivery completion
        DeliveryAssignment assignment = DeliveryAssignment.builder()
                .id(50L)
                .order(order)
                .deliveryAgent(agent)
                .deliveryStatus(DeliveryStatus.PICKED_UP)
                .pickedUpAt(LocalDateTime.now().minusMinutes(20))
                .deliveryRating(5.0)
                .build();

        // Make estimated delivery time in the past relative to now (representing a late delivery)
        order.setEstimatedDeliveryTime(LocalDateTime.now().minusMinutes(5));

        when(deliveryAssignmentRepository.findById(50L)).thenReturn(Optional.of(assignment));
        when(deliveryAssignmentRepository.save(any(DeliveryAssignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        deliveryAssignmentService.deliverOrder(50L, 5.0);

        // Assert: Rating should be decremented from 5.0 to 4.0 due to late penalty
        assertEquals(4.0, assignment.getDeliveryRating());
    }

    @Test
    void testInvalidStatusTransition() {
        // PLACED -> DELIVERED directly is invalid
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.validateStatusTransition(OrderStatus.PLACED, OrderStatus.DELIVERED);
        });

        assertTrue(exception.getMessage().contains("Cannot transition PLACED to"));
    }

    @Test
    void testValidStatusTransitions() {
        // Transitions should pass without exceptions
        assertDoesNotThrow(() -> orderService.validateStatusTransition(OrderStatus.PLACED, OrderStatus.PREPARING));
        assertDoesNotThrow(() -> orderService.validateStatusTransition(OrderStatus.PREPARING, OrderStatus.ASSIGNED));
        assertDoesNotThrow(() -> orderService.validateStatusTransition(OrderStatus.ASSIGNED, OrderStatus.PICKED_UP));
        assertDoesNotThrow(() -> orderService.validateStatusTransition(OrderStatus.PICKED_UP, OrderStatus.DELIVERED));
    }
}
