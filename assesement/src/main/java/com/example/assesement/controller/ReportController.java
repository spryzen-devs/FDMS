package com.example.assesement.controller;

import com.example.assesement.model.DTOs.*;
import com.example.assesement.service.PerformanceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private PerformanceReportService performanceReportService;

    @GetMapping("/top-agents")
    public ResponseEntity<List<TopAgentReport>> getTopAgentsReport() {
        return ResponseEntity.ok(performanceReportService.getTopAgentsReport());
    }

    @GetMapping("/delayed-deliveries")
    public ResponseEntity<List<DelayReport>> getDelayedDeliveriesReport() {
        return ResponseEntity.ok(performanceReportService.getDelayedDeliveriesReport());
    }

    @GetMapping("/restaurant-delays")
    public ResponseEntity<List<RestaurantPerformanceReport>> getRestaurantPerformanceReport() {
        return ResponseEntity.ok(performanceReportService.getRestaurantPerformanceReport());
    }

    @GetMapping("/average-delivery-time")
    public ResponseEntity<Map<String, Object>> getAverageDeliveryTimeMinutes() {
        double average = performanceReportService.getAverageDeliveryTimeMinutes();
        Map<String, Object> response = new HashMap<>();
        response.put("averageDeliveryTimeMinutes", average);
        return ResponseEntity.ok(response);
    }
}
