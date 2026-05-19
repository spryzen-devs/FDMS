package com.example.assesement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_performance", indexes = {
    @Index(name = "idx_perf_assignment", columnList = "delivery_assignment_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_assignment_id", nullable = false, unique = true)
    private DeliveryAssignment deliveryAssignment;

    @Column(name = "delivery_time_minutes")
    private Integer deliveryTimeMinutes;

    @Column(name = "was_late", nullable = false)
    private Boolean wasLate;

    @Column(name = "penalty_points")
    @Builder.Default
    private Integer penaltyPoints = 0;

    @Column(name = "customer_rating")
    private Double customerRating;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
