package com.example.assesement.service;

import com.example.assesement.entity.*;
import com.example.assesement.model.DTOs.*;
import com.example.assesement.model.Role;
import com.example.assesement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerformanceReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryPerformanceRepository deliveryPerformanceRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DeliveryAssignmentRepository deliveryAssignmentRepository;

    public List<TopAgentReport> getTopAgentsReport() {
        List<User> agents = userRepository.findByRoleAndIsActiveTrue(Role.DELIVERY_AGENT);
        List<TopAgentReport> reports = new ArrayList<>();

        for (User agent : agents) {
            long completed = deliveryPerformanceRepository.countByAgentId(agent.getId());
            if (completed == 0) {
                continue;
            }

            long late = deliveryPerformanceRepository.countLateByAgentId(agent.getId());
            long onTime = completed - late;
            double onTimeRate = ((double) onTime / completed) * 100.0;

            List<DeliveryAssignment> assignments = deliveryAssignmentRepository.findByDeliveryAgent(agent);
            double avgRating = assignments.stream()
                    .mapToDouble(DeliveryAssignment::getDeliveryRating)
                    .average()
                    .orElse(5.0);

            reports.add(TopAgentReport.builder()
                    .agentId(agent.getId())
                    .name(agent.getName())
                    .currentRating(avgRating)
                    .completedDeliveriesCount(completed)
                    .onTimeDeliveriesCount(onTime)
                    .onTimeRate(onTimeRate)
                    .build());
        }

        reports.sort((r1, r2) -> {
            int cmp = Double.compare(r2.getOnTimeRate(), r1.getOnTimeRate());
            if (cmp != 0) return cmp;
            return Double.compare(r2.getCurrentRating(), r1.getCurrentRating());
        });

        return reports;
    }

    public List<DelayReport> getDelayedDeliveriesReport() {
        List<DeliveryPerformance> performances = deliveryPerformanceRepository.findAll();
        List<DelayReport> reports = new ArrayList<>();

        for (DeliveryPerformance dp : performances) {
            if (dp.getWasLate()) {
                DeliveryAssignment da = dp.getDeliveryAssignment();
                Order order = da.getOrder();

                long estimatedMins = Duration.between(order.getPlacedAt(), order.getEstimatedDeliveryTime()).toMinutes();
                long actualMins = Duration.between(order.getPlacedAt(), da.getDeliveredAt()).toMinutes();
                long delayMins = actualMins - estimatedMins;

                reports.add(DelayReport.builder()
                        .orderId(order.getId())
                        .restaurantName(order.getRestaurant().getName())
                        .agentName(da.getDeliveryAgent().getName())
                        .estimatedMinutes((int) estimatedMins)
                        .actualMinutes((int) actualMins)
                        .delayMinutes((int) delayMins)
                        .build());
            }
        }

        return reports;
    }

    public List<RestaurantPerformanceReport> getRestaurantPerformanceReport() {
        return restaurantRepository.findAll().stream()
                .map(r -> RestaurantPerformanceReport.builder()
                        .restaurantId(r.getId())
                        .restaurantName(r.getName())
                        .averagePrepTimeMinutes(r.getAveragePreparationTime())
                        .delayCount(r.getDelayCount())
                        .build())
                .sorted((r1, r2) -> Integer.compare(r2.getDelayCount(), r1.getDelayCount()))
                .collect(Collectors.toList());
    }

    public double getAverageDeliveryTimeMinutes() {
        List<DeliveryPerformance> performances = deliveryPerformanceRepository.findAll();
        return performances.stream()
                .mapToDouble(DeliveryPerformance::getDeliveryTimeMinutes)
                .average()
                .orElse(0.0);
    }
}
