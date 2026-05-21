package com.app.supply_chain.WarehouseTransfer.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private Long productId;
    private Long sourceWarehouseId;
    private Long destinationWarehouseId;
    private int quantity;
}