package com.example.assesement.entity;

import com.example.assesement.model.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_assignments", indexes = {
    @Index(name = "idx_delivery_agent", columnList = "delivery_agent_id"),
    @Index(name = "idx_delivery_order", columnList = "order_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_agent_id", nullable = false)
    private User deliveryAgent;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 30)
    private DeliveryStatus deliveryStatus;

    @Column(name = "distance_score")
    private Double distanceScore;

    @Column(name = "delivery_rating")
    @Builder.Default
    private Double deliveryRating = 5.0;

    @Column(name = "customer_confirmed")
    @Builder.Default
    private Boolean customerConfirmed = false;
}
