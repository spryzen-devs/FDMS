package com.example.assesement.service;

import com.example.assesement.entity.*;
import com.example.assesement.model.DTOs.*;
import com.example.assesement.model.OrderStatus;
import com.example.assesement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private GridSimulationService gridSimulationService;

    @Transactional
    public OrderResponse placeOrder(OrderRequest req) {
        User customer = userRepository.findById(req.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Restaurant restaurant = restaurantRepository.findById(req.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        int dx = req.getDeliveryX() != null ? req.getDeliveryX() : customer.getX();
        int dy = req.getDeliveryY() != null ? req.getDeliveryY() : customer.getY();

        if (!gridSimulationService.isValidLocation(dx, dy)) {
            throw new RuntimeException("Invalid delivery coordinates. Must be between 0 and 100.");
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        Order order = Order.builder()
                .customer(customer)
                .restaurant(restaurant)
                .deliveryAddress(req.getDeliveryAddress())
                .orderStatus(OrderStatus.PLACED)
                .deliveryX(dx)
                .deliveryY(dy)
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(restaurant.getAveragePreparationTime() + 15))
                .build();

        for (OrderItemRequest itemReq : req.getItems()) {
            FoodItem foodItem = foodItemRepository.findById(itemReq.getFoodItemId())
                    .orElseThrow(() -> new RuntimeException("Food item not found: " + itemReq.getFoodItemId()));

            if (!foodItem.getRestaurant().getId().equals(restaurant.getId())) {
                throw new RuntimeException("Food item must belong to the selected restaurant");
            }

            BigDecimal subtotal = foodItem.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .foodItem(foodItem)
                    .quantity(itemReq.getQuantity())
                    .subtotal(subtotal)
                    .build();

            items.add(orderItem);
        }

        order.setTotalAmount(total);
        order.setOrderItems(items);

        Order saved = orderRepository.save(order);
        return convertToResponse(saved);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getCustomerOrders(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return orderRepository.findByCustomer(customer).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus nextStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        validateStatusTransition(order.getOrderStatus(), nextStatus);

        order.setOrderStatus(nextStatus);

        if (nextStatus == OrderStatus.PICKED_UP) {
            order.setPickedUpAt(LocalDateTime.now());
        } else if (nextStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        Order updated = orderRepository.save(order);
        return convertToResponse(updated);
    }

    public void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == next) return;

        switch (current) {
            case PLACED:
                if (next != OrderStatus.PREPARING && next != OrderStatus.CANCELLED) {
                    throw new RuntimeException("Cannot transition PLACED to " + next);
                }
                break;
            case PREPARING:
                if (next != OrderStatus.ASSIGNED && next != OrderStatus.CANCELLED) {
                    throw new RuntimeException("Cannot transition PREPARING to " + next);
                }
                break;
            case ASSIGNED:
                if (next != OrderStatus.PICKED_UP && next != OrderStatus.CANCELLED) {
                    throw new RuntimeException("Cannot transition ASSIGNED to " + next);
                }
                break;
            case PICKED_UP:
                if (next != OrderStatus.DELIVERED) {
                    throw new RuntimeException("Cannot transition PICKED_UP to " + next + ". Only DELIVERED is allowed.");
                }
                break;
            case DELIVERED:
                throw new RuntimeException("DELIVERED order status is final and cannot be modified.");
            case CANCELLED:
                throw new RuntimeException("CANCELLED order status is final and cannot be modified.");
        }
    }

    public OrderResponse convertToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .foodItemId(item.getFoodItem().getId())
                        .foodItemName(item.getFoodItem().getName())
                        .price(item.getFoodItem().getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .orderStatus(order.getOrderStatus())
                .placedAt(order.getPlacedAt())
                .pickedUpAt(order.getPickedUpAt())
                .deliveredAt(order.getDeliveredAt())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .deliveryX(order.getDeliveryX())
                .deliveryY(order.getDeliveryY())
                .orderItems(itemResponses)
                .build();
    }
}
