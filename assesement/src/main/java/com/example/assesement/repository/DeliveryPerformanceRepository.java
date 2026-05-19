package com.example.assesement.repository;

import com.example.assesement.entity.DeliveryPerformance;
import com.example.assesement.entity.DeliveryAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DeliveryPerformanceRepository extends JpaRepository<DeliveryPerformance, Long> {
    
    Optional<DeliveryPerformance> findByDeliveryAssignment(DeliveryAssignment deliveryAssignment);
    
    @Query("SELECT COUNT(dp) FROM DeliveryPerformance dp WHERE dp.deliveryAssignment.deliveryAgent.id = :agentId")
    long countByAgentId(@Param("agentId") Long agentId);
    
    @Query("SELECT COUNT(dp) FROM DeliveryPerformance dp WHERE dp.deliveryAssignment.deliveryAgent.id = :agentId AND dp.wasLate = true")
    long countLateByAgentId(@Param("agentId") Long agentId);
}
