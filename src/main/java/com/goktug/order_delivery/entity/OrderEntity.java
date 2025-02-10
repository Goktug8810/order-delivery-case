package com.goktug.order_delivery.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private LocalDateTime collectionStartedAt;
    private LocalDateTime collectedAt;
    private LocalDateTime deliveryStartedAt;
    private LocalDateTime deliveredAt;
    private Integer eta;
    private Long customerId;
}
