package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private String orderId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private String userId;
    @JoinColumn(name = "product_name")
    private String productName;
    @JoinColumn(name = "notification_status")
    private String notificationStatus;
    @JoinColumn(name = "created_at")
    private String createdAt;

    // Getters and setters
}
