package com.app.supply_chain.order.dto;

import lombok.Data;

@Data
public class OrderRequest {

    private Long productId;
    private Long warehouseId;
    private int quantity;
    private String idempotencyKey;
}