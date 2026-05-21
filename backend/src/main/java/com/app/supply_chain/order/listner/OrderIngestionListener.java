package com.app.supply_chain.order.listner;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.app.supply_chain.order.dto.OrderRequest;
import com.app.supply_chain.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;   
@Component
public class OrderIngestionListener {

    private final OrderService orderService;

    public OrderIngestionListener(OrderService orderService) {
        this.orderService = orderService;
    }

    // Set concurrency high so Kafka pulls fast from the queue
    // public void handleIngestion(OrderRequest request) {
    //         orderService.processOrderPersist(request);
    //     }
//         @KafkaListener(topics = "order-ingestion-v4", groupId = "ingestion-group-v4", containerFactory="batchFactory")
//     public void consume(List<OrderRequest> requests) {
//          long now = System.currentTimeMillis();

//     for (OrderRequest r : requests) {
//         System.out.println("KAFKA_CONSUMED | key=" + r.getIdempotencyKey() + " | time=" + now);
//     }
//     orderService.processBatch(requests);
// }

private final ObjectMapper objectMapper = new ObjectMapper();

@KafkaListener(
    topics = "order-ingestion-v4",
    containerFactory = "kafkaListenerContainerFactory",
    concurrency= "12"
)
public void consume(List<OrderRequest> requests) {
    orderService.processBatch(requests);
}
}