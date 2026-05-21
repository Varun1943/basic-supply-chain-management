package com.app.supply_chain.inventory.dto;
import lombok.Data;

@Data
public class InventoryRequest {
    private Long id;
    private Long productId;
    private Long warehouseId;
    private int quantity;
}       
