package com.app.supply_chain.shipments.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import com.app.supply_chain.order.model.Order;

@Data
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String status;

    private String carrier;

    private String trackingNumber;

    private LocalDateTime estimatedDelivery;
}