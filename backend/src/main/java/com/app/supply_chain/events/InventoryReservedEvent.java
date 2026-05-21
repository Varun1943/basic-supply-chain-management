package com.app.supply_chain.events;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservedEvent {
    private String eventId;
    private Long orderId;
    private boolean success;
    private String message;
}