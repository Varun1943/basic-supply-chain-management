package com.app.supply_chain.WarehouseTransfer.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long sourceWarehouseId;

    private Long destinationWarehouseId;

    private int quantity;

    private String status; // INITIATED, COMPLETED

    private LocalDateTime createdAt;
}