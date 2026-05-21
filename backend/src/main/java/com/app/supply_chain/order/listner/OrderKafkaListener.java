package com.app.supply_chain.order.listner; 
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.app.supply_chain.events.InventoryReservedEvent;
import com.app.supply_chain.order.model.Order;
import com.app.supply_chain.order.repository.OrderRepository;

@Component
public class OrderKafkaListener {

    private final OrderRepository orderRepository;

    public OrderKafkaListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "inventory-events-v4", groupId = "order-group-v4",concurrency="12")
    public void handle(InventoryReservedEvent event) {
// 
        // System.out.println(" Order received inventory result: " + event.getOrderId());

        Order order = orderRepository.findByIdempotencyKey(event.getEventId())
                .orElseThrow();
//                 System.out.println(
//     "ORDER_RECEIVED_RESULT | eventId=" + event.getEventId() +
//     " | orderId=" + event.getOrderId() +
//     " | success=" + event.isSuccess()
// );
//         System.out.println(
//     "ORDER_LOOKUP | searching key=" + event.getEventId()
// );
// System.out.println(
//     "ORDER_FOUND | id=" + order.getId() +
//     " | currentStatus=" + order.getStatus()
// );
        if (event.isSuccess()) {
            order.setStatus("CONFIRMED");
        } else {
            order.setStatus("FAILED");
        }
//         System.out.println(
//     "ORDER_UPDATED | newStatus=" + order.getStatus()
// );

        orderRepository.save(order);
    }
}
