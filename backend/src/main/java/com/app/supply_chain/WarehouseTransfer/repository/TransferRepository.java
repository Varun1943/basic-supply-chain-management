package com.app.supply_chain.WarehouseTransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.supply_chain.WarehouseTransfer.model.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}