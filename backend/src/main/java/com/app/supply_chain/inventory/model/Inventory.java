package com.app.supply_chain.inventory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import lombok.Data;
import jakarta.persistence.FetchType;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;

import com.app.supply_chain.product.model.Product;
import com.app.supply_chain.warehouse.model.WareHouse;
import jakarta.persistence.UniqueConstraint;
@Data
@Entity
@Table(name = "inventory",
 uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "warehouse_id"})
    }

)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private WareHouse warehouse;

    // private int quantity;,
    private int availableQuantity;
    private int reservedQuantity;
}