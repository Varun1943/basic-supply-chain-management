package com.app.supply_chain.inventory.reservation.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.supply_chain.inventory.reservation.model.InventoryReservation;
import com.app.supply_chain.inventory.reservation.model.ReservationStatus;
public interface InventoryReservationRepository
        extends JpaRepository<InventoryReservation, Long> {

    List<InventoryReservation> findByStatusAndExpiresAtBefore(
        ReservationStatus status,
        LocalDateTime time
    );

    Optional<InventoryReservation> findByOrderId(Long orderId);
}
