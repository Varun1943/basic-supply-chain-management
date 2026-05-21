package com.app.supply_chain.events;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
    private String eventId;
    private Long orderId;
    private Long productId;
    private Long warehouseId;
    private int quantity;
}