package com.app.supply_chain.order.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.supply_chain.events.OrderCreatedEvent;
import com.app.supply_chain.inventory.service.InventoryService;
import com.app.supply_chain.order.dto.OrderRequest;
import com.app.supply_chain.order.model.Order;
import com.app.supply_chain.order.repository.OrderRepository;
import com.app.supply_chain.product.model.Product;
import com.app.supply_chain.product.repository.ProductRepository;
import com.app.supply_chain.shipments.service.ShipmentService;
import com.app.supply_chain.warehouse.model.WareHouse;
import com.app.supply_chain.warehouse.repository.WareHouseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class OrderService {

    private final ShipmentService shipmentService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final WareHouseRepository warehouseRepository;
    private final InventoryService inventoryService;
    private final StringRedisTemplate redisTemplate;
    private final JdbcTemplate jdbcTemplate;
    // private final Producer<String, byte[]> kafkaProducer;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // 🔥 isolate Kafka from request thread
    // private final ExecutorService executor = Executors.newFixedThreadPool(20);

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            WareHouseRepository warehouseRepository,
            ShipmentService shipmentService,
            InventoryService inventoryService,
            StringRedisTemplate redisTemplate,
            JdbcTemplate jdbcTemplate,
            KafkaTemplate<String, Object> kafkaTemplate,
            ObjectMapper objectMapper   //  inject instead of new
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.shipmentService = shipmentService;
        this.inventoryService = inventoryService;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
public List<Order> getAllOrders() {
    return orderRepository.findAll();
}
    // ==========================
    // 🔥 KAFKA INGESTION (API PATH)
    // ==========================
public void submitOrderAsync(OrderRequest request) {

        try {
            String key = request.getProductId() + ":" + request.getWarehouseId();

           kafkaTemplate.send("order-ingestion-v4", request);

            // long start = System.currentTimeMillis();

          
    }
catch (Exception e) {
            throw new RuntimeException("Failed to submit order");
        }
    }
    


    // ==========================
    // 🔥 BATCH PROCESSING (CONSUMER)
    // ==========================
    @Transactional
    public void processBatch(List<OrderRequest> requests) {

        List<Order> orders = new ArrayList<>();

        for (OrderRequest request : requests) {

            String redisKey = "idemp:order:" + request.getIdempotencyKey();

            Boolean isNew = redisTemplate.opsForValue()
                    .setIfAbsent(redisKey, "1", Duration.ofDays(1));

            if (Boolean.FALSE.equals(isNew)) continue;

            Order order = new Order();

            Product product = new Product();
            product.setId(request.getProductId());

            WareHouse warehouse = new WareHouse();
            warehouse.setId(request.getWarehouseId());

            order.setProduct(product);
            order.setWarehouse(warehouse);
            order.setQuantity(request.getQuantity());
            order.setStatus("PENDING");
            order.setCreatedAt(LocalDateTime.now());
            order.setIdempotencyKey(request.getIdempotencyKey());

            orders.add(order);
        }

        if (orders.isEmpty()) return;

        // long dbStart = System.currentTimeMillis();

        jdbcTemplate.batchUpdate(
            "INSERT INTO orders (product_id, warehouse_id, quantity, status, created_at, idempotency_key) VALUES (?, ?, ?, ?, ?, ?)",
            orders,
            1000,
            (ps, order) -> {
                ps.setLong(1, order.getProduct().getId());
                ps.setLong(2, order.getWarehouse().getId());
                ps.setInt(3, order.getQuantity());
                ps.setString(4, order.getStatus());
                ps.setObject(5, order.getCreatedAt());
                ps.setString(6, order.getIdempotencyKey());
            }
        );

        // long dbTime = System.currentTimeMillis() - dbStart;
        // System.out.println("DB_BATCH | size=" + orders.size() + " | " + dbTime + "ms");

        // ==========================
        // 🔥 SEND INVENTORY EVENTS
        // ==========================
        for (Order order : orders) {

                try {
                    OrderCreatedEvent event = new OrderCreatedEvent(
    order.getIdempotencyKey(),  // eventId
    null,              
    order.getProduct().getId(),
    order.getWarehouse().getId(),
    order.getQuantity()
);
                    String key = order.getProduct().getId() + ":" + order.getWarehouse().getId();
// System.out.println(
//     "ORDER_SENT_TO_INVENTORY | key=" + order.getIdempotencyKey() +
//     " | orderId=" + order.getId() +
//     " | product=" + order.getProduct().getId()
// );
                    kafkaTemplate.send("order-events-v4", event);

                } catch (Exception ignored) {}
            
        }
    }

    // ==========================
    // 🔥 ORDER LIFECYCLE
    // ==========================
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        if (!order.getStatus().equals("CREATED")) {
            throw new RuntimeException("Cannot cancel this order");
        }

        inventoryService.releaseStock(
            order.getProduct().getId(),
            order.getWarehouse().getId(),
            order.getQuantity(),
            order.getId()
        );

        order.setStatus("CANCELLED");
        return orderRepository.save(order);
    }

    @Transactional
    public Order markOrderShipped(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        if (!order.getStatus().equals("CREATED")) {
            throw new RuntimeException("Cannot ship this order");
        }

        order.setStatus("SHIPPED");
        Order savedOrder = orderRepository.save(order);

        shipmentService.createShipment(savedOrder.getId());

        return savedOrder;
    }
}