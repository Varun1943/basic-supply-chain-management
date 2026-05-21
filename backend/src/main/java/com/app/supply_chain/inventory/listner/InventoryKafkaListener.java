package com.app.supply_chain.inventory.listner;
import java.util.List;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.app.supply_chain.events.InventoryReservedEvent;
import com.app.supply_chain.events.OrderCreatedEvent;
import com.app.supply_chain.inventory.service.InventoryService;

@Component
public class InventoryKafkaListener {

    private final InventoryService inventoryService;
        private final KafkaTemplate<String, Object> kafkaTemplate;
    // private final ExecutorService executor = Executors.newFixedThreadPool(20);
    public InventoryKafkaListener(InventoryService inventoryService,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.inventoryService = inventoryService;
        this.kafkaTemplate = kafkaTemplate;
    }

 @KafkaListener(
    topics = "order-events-v4",
    groupId = "inventory-group-v4",
    containerFactory = "kafkaListenerContainerFactory",
    concurrency = "12"
)
public void handle(List<OrderCreatedEvent> events) {

    for (OrderCreatedEvent event : events) {
//         System.out.println(
//     "INVENTORY_RECEIVED | orderId=" + event.getOrderId() +
//     " | product=" + event.getProductId()
// );
        processEvent(event);
    }
}
private void processEvent(OrderCreatedEvent event) {

    try {
        inventoryService.reserveStock(
            event.getProductId(),
            event.getWarehouseId(),
            event.getQuantity()
            // event.getOrderId()
        );
    // System.out.println("Inventory received event: " + event.getEventId());
        kafkaTemplate.send("inventory-events-v4",
            event.getEventId(),
            new InventoryReservedEvent(
                event.getEventId(),   
    null,
    true,
    "reserved"
            ));

    } catch (Exception e) {
//     e.printStackTrace();   
//         System.out.println(
//     "INVENTORY_RESULT_SENT | orderId=" + event.getOrderId() +
//     " | success=false"
// );
        kafkaTemplate.send("inventory-events-v4",
            event.getOrderId().toString(),
            new InventoryReservedEvent(
                UUID.randomUUID().toString(),
                event.getOrderId(),
                false,
                e.getMessage()
            ));
    }
}
}