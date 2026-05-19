package com.example.assesement.controller;

import com.example.assesement.model.DTOs.*;
import com.example.assesement.service.DeliveryAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "*")
public class DeliveryAssignmentController {

    @Autowired
    private DeliveryAssignmentService deliveryAssignmentService;

    @GetMapping("/recommend")
    public ResponseEntity<List<AgentRecommendation>> recommendAgents(@RequestParam Long orderId) {
        return ResponseEntity.ok(deliveryAssignmentService.recommendAgents(orderId));
    }

    @PostMapping("/assign")
    public ResponseEntity<DeliveryAssignmentResponse> assignAgent(
            @RequestParam Long orderId,
            @RequestParam Long agentId) {
        return ResponseEntity.ok(deliveryAssignmentService.assignAgent(orderId, agentId));
    }

    @PutMapping("/{id}/pickup")
    public ResponseEntity<DeliveryAssignmentResponse> pickupOrder(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryAssignmentService.pickupOrder(id));
    }

    @PutMapping("/{id}/deliver")
    public ResponseEntity<DeliveryAssignmentResponse> deliverOrder(
            @PathVariable Long id,
            @RequestParam(required = false) Double rating) {
        return ResponseEntity.ok(deliveryAssignmentService.deliverOrder(id, rating));
    }

    @PutMapping("/{id}/customer-confirm")
    public ResponseEntity<DeliveryAssignmentResponse> customerConfirmDelivery(
            @PathVariable Long id,
            @RequestParam(required = false) Double rating) {
        return ResponseEntity.ok(deliveryAssignmentService.customerConfirmDelivery(id, rating));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryAssignmentResponse>> getAllAssignments() {
        return ResponseEntity.ok(deliveryAssignmentService.getAllAssignments());
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<DeliveryAssignmentResponse>> getAgentAssignments(@PathVariable Long agentId) {
        return ResponseEntity.ok(deliveryAssignmentService.getAgentAssignments(agentId));
    }
}
