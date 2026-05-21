package com.app.supply_chain.shipments.controller;

import org.springframework.web.bind.annotation.*;

import com.app.supply_chain.shipments.model.Shipment;
import com.app.supply_chain.shipments.service.ShipmentService;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping("/{orderId}")
    public Shipment createShipment(@PathVariable Long orderId) {
        return shipmentService.createShipment(orderId);
    }
    @PutMapping("/{id}/deliver")
public Shipment deliver(@PathVariable Long id) {
    return shipmentService.markDelivered(id);
}
}