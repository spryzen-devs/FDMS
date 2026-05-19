package com.example.assesement.service;

import com.example.assesement.entity.*;
import com.example.assesement.model.*;
import com.example.assesement.model.DTOs.*;
import com.example.assesement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeliveryAssignmentService {

    @Autowired
    private DeliveryAssignmentRepository deliveryAssignmentRepository;

    @Autowired
    private DeliveryPerformanceRepository deliveryPerformanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private GridSimulationService gridSimulationService;

    @Autowired
    private OrderService orderService;

    private static final List<DeliveryStatus> ACTIVE_STATUSES = Arrays.asList(
            DeliveryStatus.ASSIGNED,
            DeliveryStatus.PICKED_UP
    );

    public List<AgentRecommendation> recommendAgents(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Restaurant restaurant = order.getRestaurant();
        List<User> agents = userRepository.findByRoleAndIsActiveTrue(Role.DELIVERY_AGENT);

        List<AgentRecommendation> recommendations = new ArrayList<>();

        for (User agent : agents) {
            boolean isBusy = deliveryAssignmentRepository
                    .existsByDeliveryAgentIdAndDeliveryStatusIn(agent.getId(), ACTIVE_STATUSES);

            if (isBusy) {
                continue;
            }

            long totalCompleted = deliveryPerformanceRepository.countByAgentId(agent.getId());
            long lateDeliveries = deliveryPerformanceRepository.countLateByAgentId(agent.getId());
            long activeOrders = deliveryAssignmentRepository.countByDeliveryAgentIdAndDeliveryStatusIn(agent.getId(), ACTIVE_STATUSES);

            double distance = gridSimulationService.calculateDistance(agent.getX(), agent.getY(), restaurant.getX(), restaurant.getY());

            double agentRating = 5.0;
            List<DeliveryAssignment> history = deliveryAssignmentRepository.findByDeliveryAgent(agent);
            if (!history.isEmpty()) {
                agentRating = history.stream()
                        .mapToDouble(DeliveryAssignment::getDeliveryRating)
                        .average()
                        .orElse(5.0);
            }

            double score = (agentRating * 50) - (lateDeliveries * 20) - (activeOrders * 30) - (distance * 10);

            if (agent.getZone() != null && agent.getZone().equalsIgnoreCase(restaurant.getLocation())) {
                score += 25;
            }

            recommendations.add(AgentRecommendation.builder()
                    .agentId(agent.getId())
                    .agentName(agent.getName())
                    .rating(agentRating)
                    .distance(distance)
                    .score(score)
                    .build());
        }

        recommendations.sort((r1, r2) -> Double.compare(r2.getScore(), r1.getScore()));
        return recommendations;
    }

    @Transactional
    public DeliveryAssignmentResponse assignAgent(Long orderId, Long agentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        if (agent.getRole() != Role.DELIVERY_AGENT) {
            throw new RuntimeException("Specified user is not a delivery agent");
        }

        boolean isBusy = deliveryAssignmentRepository
                .existsByDeliveryAgentIdAndDeliveryStatusIn(agent.getId(), ACTIVE_STATUSES);

        if (isBusy) {
            throw new RuntimeException("Agent already handling active order");
        }

        Optional<DeliveryAssignment> existingOpt = deliveryAssignmentRepository.findByOrder(order);
        if (existingOpt.isPresent()) {
            DeliveryAssignment existing = existingOpt.get();
            if (existing.getDeliveryStatus() == DeliveryStatus.PICKED_UP) {
                throw new RuntimeException("Cannot reassign after pickup");
            }
            deliveryAssignmentRepository.delete(existing);
        }

        double distance = gridSimulationService.calculateDistance(agent.getX(), agent.getY(), order.getRestaurant().getX(), order.getRestaurant().getY());

        DeliveryAssignment assignment = DeliveryAssignment.builder()
                .order(order)
                .deliveryAgent(agent)
                .assignedAt(LocalDateTime.now())
                .deliveryStatus(DeliveryStatus.ASSIGNED)
                .distanceScore(distance)
                .deliveryRating(5.0)
                .build();

        orderService.updateOrderStatus(orderId, OrderStatus.ASSIGNED);

        DeliveryAssignment saved = deliveryAssignmentRepository.save(assignment);
        return convertToResponse(saved);
    }

    @Transactional
    public DeliveryAssignmentResponse pickupOrder(Long assignmentId) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (assignment.getDeliveryStatus() != DeliveryStatus.ASSIGNED) {
            throw new RuntimeException("Assignment must be in ASSIGNED status to pick up");
        }

        LocalDateTime now = LocalDateTime.now();
        assignment.setPickedUpAt(now);
        assignment.setDeliveryStatus(DeliveryStatus.PICKED_UP);

        Order order = assignment.getOrder();
        orderService.updateOrderStatus(order.getId(), OrderStatus.PICKED_UP);

        long prepMinutes = Duration.between(order.getPlacedAt(), now).toMinutes();
        Restaurant restaurant = order.getRestaurant();
        if (prepMinutes > restaurant.getAveragePreparationTime()) {
            restaurant.setDelayCount(restaurant.getDelayCount() + 1);
            restaurantRepository.save(restaurant);
        }

        DeliveryAssignment saved = deliveryAssignmentRepository.save(assignment);
        return convertToResponse(saved);
    }

    @Transactional
    public DeliveryAssignmentResponse deliverOrder(Long assignmentId, Double customerRating) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (assignment.getDeliveryStatus() != DeliveryStatus.PICKED_UP) {
            throw new RuntimeException("Order must be PICKED_UP before it can be DELIVERED");
        }

        LocalDateTime now = LocalDateTime.now();
        assignment.setDeliveredAt(now);
        assignment.setDeliveryStatus(DeliveryStatus.DELIVERED);
        
        if (customerRating != null) {
            assignment.setDeliveryRating(customerRating);
        }

        Order order = assignment.getOrder();
        orderService.updateOrderStatus(order.getId(), OrderStatus.DELIVERED);

        long deliveryMinutes = Duration.between(assignment.getPickedUpAt(), now).toMinutes();

        boolean wasLate = now.isAfter(order.getEstimatedDeliveryTime());

        int penaltyPoints = 0;
        if (wasLate) {
            penaltyPoints = 5;
            assignment.setDeliveryRating(Math.max(1.0, assignment.getDeliveryRating() - 1.0));
        }

        DeliveryAssignment savedAssignment = deliveryAssignmentRepository.save(assignment);

        DeliveryPerformance performance = DeliveryPerformance.builder()
                .deliveryAssignment(savedAssignment)
                .deliveryTimeMinutes((int) deliveryMinutes)
                .wasLate(wasLate)
                .penaltyPoints(penaltyPoints)
                .customerRating(assignment.getDeliveryRating())
                .createdAt(now)
                .build();

        deliveryPerformanceRepository.save(performance);

        return convertToResponse(savedAssignment);
    }

    public List<DeliveryAssignmentResponse> getAllAssignments() {
        return deliveryAssignmentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<DeliveryAssignmentResponse> getAgentAssignments(Long agentId) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        return deliveryAssignmentRepository.findByDeliveryAgent(agent).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryAssignmentResponse customerConfirmDelivery(Long assignmentId, Double customerRating) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (assignment.getDeliveryStatus() != DeliveryStatus.DELIVERED) {
            throw new RuntimeException("Order must be marked as delivered by agent first");
        }

        assignment.setCustomerConfirmed(true);
        if (customerRating != null) {
            assignment.setDeliveryRating(customerRating);

            Order order = assignment.getOrder();
            boolean wasLate = assignment.getDeliveredAt().isAfter(order.getEstimatedDeliveryTime());
            if (wasLate) {
                assignment.setDeliveryRating(Math.max(1.0, customerRating - 1.0));
            }

            Optional<DeliveryPerformance> perfOpt = deliveryPerformanceRepository.findByDeliveryAssignment(assignment);
            if (perfOpt.isPresent()) {
                DeliveryPerformance performance = perfOpt.get();
                performance.setCustomerRating(assignment.getDeliveryRating());
                deliveryPerformanceRepository.save(performance);
            }
        }

        DeliveryAssignment saved = deliveryAssignmentRepository.save(assignment);
        return convertToResponse(saved);
    }

    public DeliveryAssignmentResponse convertToResponse(DeliveryAssignment da) {
        return DeliveryAssignmentResponse.builder()
                .id(da.getId())
                .orderId(da.getOrder().getId())
                .deliveryAgentId(da.getDeliveryAgent().getId())
                .deliveryAgentName(da.getDeliveryAgent().getName())
                .assignedAt(da.getAssignedAt())
                .pickedUpAt(da.getPickedUpAt())
                .deliveredAt(da.getDeliveredAt())
                .deliveryStatus(da.getDeliveryStatus())
                .distanceScore(da.getDistanceScore())
                .deliveryRating(da.getDeliveryRating())
                .customerConfirmed(da.getCustomerConfirmed())
                .build();
    }
}
