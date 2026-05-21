package com.app.supply_chain.inventory.reservation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.supply_chain.inventory.repository.InventoryRepository;
import com.app.supply_chain.inventory.reservation.model.InventoryReservation;
import com.app.supply_chain.inventory.reservation.model.ReservationStatus;
import com.app.supply_chain.inventory.reservation.repository.InventoryReservationRepository;

@Service
public class ReservationExpiryService {

    private final InventoryReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;

    public ReservationExpiryService(
        InventoryReservationRepository reservationRepository,
        InventoryRepository inventoryRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireReservations() {

        List<InventoryReservation> expired =
            reservationRepository.findByStatusAndExpiresAtBefore(
                ReservationStatus.ACTIVE,
                LocalDateTime.now()
            );

        for (InventoryReservation r : expired) {

    if (!r.getStatus().equals(ReservationStatus.ACTIVE)) {
        continue;
    }

    int updated = inventoryRepository.releaseStockAtomic(
        r.getProductId(),
        r.getWarehouseId(),
        r.getQuantity()
    );

    if (updated == 0) {
        throw new RuntimeException("Invalid expiry");
    }

    r.setStatus(ReservationStatus.EXPIRED);
    reservationRepository.save(r);
}
    }
}