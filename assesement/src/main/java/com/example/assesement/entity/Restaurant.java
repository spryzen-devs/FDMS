package com.example.assesement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurants", indexes = {
    @Index(name = "idx_restaurant_manager", columnList = "manager_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "average_preparation_time")
    @Builder.Default
    private Integer averagePreparationTime = 15;

    @Builder.Default
    private Double rating = 5.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @Column(name = "coord_x")
    @Builder.Default
    private Integer x = 0;

    @Column(name = "coord_y")
    @Builder.Default
    private Integer y = 0;

    @Column(name = "delay_count")
    @Builder.Default
    private Integer delayCount = 0;
}
