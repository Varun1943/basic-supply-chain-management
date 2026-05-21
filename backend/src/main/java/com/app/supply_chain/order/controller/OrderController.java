package com.app.supply_chain.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.supply_chain.order.dto.OrderRequest;
import com.app.supply_chain.order.model.Order;
import com.app.supply_chain.order.service.OrderService;
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest request) {
        // Send to Kafka, do NOT touch the database here
        // orderService.submitOrderAsync(request);
        // return ResponseEntity.accepted().build();
        // long start = System.currentTimeMillis();

    orderService.submitOrderAsync(request);

    // long total = System.currentTimeMillis() - start;

    // System.out.println("API_LATENCY | key=" + request.getIdempotencyKey() + " | " + total + "ms");

    return ResponseEntity.accepted().build();
    }

    @PutMapping("/{id}/cancel")
    public Order cancel(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }
//     @GetMapping
//     public List<Order> getAllOrders() {
//         return orderService.getAllOrders();
// }

    @PutMapping("/{id}/ship")
    public Order ship(@PathVariable Long id) {
        return orderService.markOrderShipped(id);
    }
}