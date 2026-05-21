package com.app.supply_chain.shipments.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import com.app.supply_chain.shipments.model.Shipment;
import com.app.supply_chain.shipments.repository.ShipmentRepository;
import com.app.supply_chain.order.model.Order;
import com.app.supply_chain.order.repository.OrderRepository;
import com.app.supply_chain.inventory.model.Inventory;
import org.springframework.transaction.annotation.Transactional;
import com.app.supply_chain.inventory.repository.InventoryRepository;
import com.app.supply_chain.inventory.service.InventoryService;
@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    public ShipmentService(ShipmentRepository shipmentRepository,
                           OrderRepository orderRepository,
                           InventoryRepository inventoryRepository,
                           InventoryService inventoryService) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryService = inventoryService;
    }

    public Shipment createShipment(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow();

        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setStatus("CREATED");
        shipment.setCarrier("DELIVERY");
        shipment.setTrackingNumber("TRK-" + System.currentTimeMillis());
        shipment.setEstimatedDelivery(LocalDateTime.now().plusDays(3));

        return shipmentRepository.save(shipment);
    }
    @Transactional
    public Shipment markDelivered(Long shipmentId) {

    Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow();

    if (!shipment.getStatus().equals("CREATED")) {
        throw new RuntimeException("Cannot deliver this shipment");
    }

    Order order = shipment.getOrder();

    Inventory inventory = inventoryRepository
            .findByProductAndWarehouse(
                    order.getProduct(),
                    order.getWarehouse())
            .orElseThrow();

inventoryService.confirmStock(
    order.getProduct().getId(),
    order.getWarehouse().getId(),
    order.getQuantity(),
    order.getId()
);
    inventoryRepository.save(inventory);

    shipment.setStatus("DELIVERED");

    return shipmentRepository.save(shipment);
}
}