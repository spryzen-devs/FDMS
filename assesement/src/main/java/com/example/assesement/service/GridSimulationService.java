package com.example.assesement.service;

import com.example.assesement.entity.DeliveryAssignment;
import com.example.assesement.entity.User;
import com.example.assesement.model.DeliveryStatus;
import com.example.assesement.repository.DeliveryAssignmentRepository;
import com.example.assesement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
public class GridSimulationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryAssignmentRepository deliveryAssignmentRepository;

    public double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public boolean isValidLocation(int x, int y) {
        return x >= 0 && x <= 100 && y >= 0 && y <= 100;
    }

    @Transactional
    public void stepSimulation() {
        List<DeliveryAssignment> activeAssignments = deliveryAssignmentRepository
                .findActiveAssignmentsForAgent(null, Arrays.asList(DeliveryStatus.ASSIGNED, DeliveryStatus.PICKED_UP));

        List<DeliveryAssignment> allActive = deliveryAssignmentRepository.findAll().stream()
                .filter(da -> da.getDeliveryStatus() == DeliveryStatus.ASSIGNED || da.getDeliveryStatus() == DeliveryStatus.PICKED_UP)
                .toList();

        for (DeliveryAssignment da : allActive) {
            User agent = da.getDeliveryAgent();
            int targetX, targetY;

            if (da.getDeliveryStatus() == DeliveryStatus.ASSIGNED) {
                targetX = da.getOrder().getRestaurant().getX();
                targetY = da.getOrder().getRestaurant().getY();
            } else {
                targetX = da.getOrder().getDeliveryX();
                targetY = da.getOrder().getDeliveryY();
            }

            int currentX = agent.getX();
            int currentY = agent.getY();

            double distance = calculateDistance(currentX, currentY, targetX, targetY);
            if (distance <= 15) {
                agent.setX(targetX);
                agent.setY(targetY);
            } else {
                double ratio = 15.0 / distance;
                int newX = (int) Math.round(currentX + (targetX - currentX) * ratio);
                int newY = (int) Math.round(currentY + (targetY - currentY) * ratio);
                agent.setX(newX);
                agent.setY(newY);
            }
            userRepository.save(agent);
        }
    }
}
