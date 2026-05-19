package com.example.assesement.repository;

import com.example.assesement.entity.DeliveryAssignment;
import com.example.assesement.entity.User;
import com.example.assesement.entity.Order;
import com.example.assesement.model.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, Long> {
    
    Optional<DeliveryAssignment> findByOrder(Order order);
    List<DeliveryAssignment> findByDeliveryAgent(User deliveryAgent);
    
    @Query("SELECT COUNT(da) > 0 FROM DeliveryAssignment da WHERE da.deliveryAgent.id = :agentId AND da.deliveryStatus IN (:statuses)")
    boolean existsByDeliveryAgentIdAndDeliveryStatusIn(@Param("agentId") Long agentId, @Param("statuses") List<DeliveryStatus> statuses);
    
    @Query("SELECT COUNT(da) FROM DeliveryAssignment da WHERE da.deliveryAgent.id = :agentId AND da.deliveryStatus IN (:statuses)")
    long countByDeliveryAgentIdAndDeliveryStatusIn(@Param("agentId") Long agentId, @Param("statuses") List<DeliveryStatus> statuses);

    @Query("SELECT da FROM DeliveryAssignment da WHERE da.deliveryAgent.id = :agentId AND da.deliveryStatus IN (:statuses)")
    List<DeliveryAssignment> findActiveAssignmentsForAgent(@Param("agentId") Long agentId, @Param("statuses") List<DeliveryStatus> statuses);
}
