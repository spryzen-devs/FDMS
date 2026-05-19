package com.example.assesement.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
        private String phone;
        private Role role;
        private Integer x;
        private Integer y;
        private String zone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private Role role;
        private LocalDateTime createdAt;
        private Boolean isActive;
        private Integer x;
        private Integer y;
        private String zone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RestaurantRequest {
        private String name;
        private String location;
        private Integer averagePreparationTime;
        private Double rating;
        private Long managerId;
        private Integer x;
        private Integer y;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RestaurantResponse {
        private Long id;
        private String name;
        private String location;
        private Integer averagePreparationTime;
        private Double rating;
        private Long managerId;
        private Integer x;
        private Integer y;
        private Integer delayCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FoodItemRequest {
        private String name;
        private BigDecimal price;
        private String category;
        private Long restaurantId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FoodItemResponse {
        private Long id;
        private String name;
        private BigDecimal price;
        private String category;
        private Boolean isAvailable;
        private Long restaurantId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        private Long foodItemId;
        private Integer quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderRequest {
        private Long customerId;
        private Long restaurantId;
        private String deliveryAddress;
        private List<OrderItemRequest> items;
        private Integer deliveryX;
        private Integer deliveryY;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long foodItemId;
        private String foodItemName;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderResponse {
        private Long id;
        private Long customerId;
        private String customerName;
        private Long restaurantId;
        private String restaurantName;
        private BigDecimal totalAmount;
        private String deliveryAddress;
        private OrderStatus orderStatus;
        private LocalDateTime placedAt;
        private LocalDateTime pickedUpAt;
        private LocalDateTime deliveredAt;
        private LocalDateTime estimatedDeliveryTime;
        private Integer deliveryX;
        private Integer deliveryY;
        private List<OrderItemResponse> orderItems;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeliveryAssignmentResponse {
        private Long id;
        private Long orderId;
        private Long deliveryAgentId;
        private String deliveryAgentName;
        private LocalDateTime assignedAt;
        private LocalDateTime pickedUpAt;
        private LocalDateTime deliveredAt;
        private DeliveryStatus deliveryStatus;
        private Double distanceScore;
        private Double deliveryRating;
        private Boolean customerConfirmed;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgentRecommendation {
        private Long agentId;
        private String agentName;
        private Double rating;
        private Double distance;
        private Double score;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopAgentReport {
        private Long agentId;
        private String name;
        private Double currentRating;
        private Long completedDeliveriesCount;
        private Long onTimeDeliveriesCount;
        private Double onTimeRate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DelayReport {
        private Long orderId;
        private String restaurantName;
        private String agentName;
        private Integer estimatedMinutes;
        private Integer actualMinutes;
        private Integer delayMinutes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RestaurantPerformanceReport {
        private Long restaurantId;
        private String restaurantName;
        private Integer averagePrepTimeMinutes;
        private Integer delayCount;
    }
}
