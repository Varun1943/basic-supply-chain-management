package com.app.supply_chain.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.supply_chain.inventory.model.Inventory;
import com.app.supply_chain.product.model.Product;
import com.app.supply_chain.warehouse.model.WareHouse;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductAndWarehouse(Product product, WareHouse warehouse);
        

@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.warehouse.id = :warehouseId")
    Optional<Inventory> findByProductAndWarehouseForUpdate(Long productId, Long warehouseId);

@Modifying
@Transactional
@Query("""
UPDATE Inventory i
SET i.availableQuantity = i.availableQuantity - :qty,
    i.reservedQuantity = i.reservedQuantity + :qty
WHERE i.product.id = :productId
AND i.warehouse.id = :warehouseId
AND i.availableQuantity >= :qty
""")
int reserveStockAtomic(Long productId, Long warehouseId, int qty);


@Modifying
@Transactional
@Query("""
UPDATE Inventory i
SET i.reservedQuantity = i.reservedQuantity - :qty,
    i.availableQuantity = i.availableQuantity + :qty
WHERE i.product.id = :productId
AND i.warehouse.id = :warehouseId
AND i.reservedQuantity >= :qty
""")
int releaseStockAtomic(Long productId, Long warehouseId, int qty);


@Modifying
@Transactional
@Query("""
UPDATE Inventory i
SET i.reservedQuantity = i.reservedQuantity - :qty
WHERE i.product.id = :productId
AND i.warehouse.id = :warehouseId
AND i.reservedQuantity >= :qty
""")
int confirmStockAtomic(Long productId, Long warehouseId, int qty);
}