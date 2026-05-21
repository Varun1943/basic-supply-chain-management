package com.app.supply_chain.shipments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.supply_chain.shipments.model.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}