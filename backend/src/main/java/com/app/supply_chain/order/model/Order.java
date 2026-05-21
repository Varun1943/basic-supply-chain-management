package com.app.supply_chain.order.model;

import java.time.LocalDateTime;

import com.app.supply_chain.product.model.Product;
import com.app.supply_chain.warehouse.model.WareHouse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private WareHouse warehouse;

    private int quantity;

    private String status;
    @Column(unique = true)
    private String idempotencyKey;
    private LocalDateTime createdAt;
}