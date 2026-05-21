package com.app.supply_chain.WarehouseTransfer.service;
import org.springframework.stereotype.Service;
import com.app.supply_chain.inventory.model.Inventory;
import com.app.supply_chain.inventory.service.InventoryService;
import com.app.supply_chain.inventory.repository.InventoryRepository;
import com.app.supply_chain.WarehouseTransfer.repository.TransferRepository;
import com.app.supply_chain.WarehouseTransfer.model.Transfer;
import com.app.supply_chain.WarehouseTransfer.dto.TransferRequest;
import com.app.supply_chain.product.repository.ProductRepository;
import com.app.supply_chain.warehouse.repository.WareHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final TransferRepository transferRepository;
    private final ProductRepository productRepository;
    private final WareHouseRepository warehouseRepository;

    
    @Transactional
    public Transfer transferStock(TransferRequest request) {

        inventoryService.transferStock(
    request.getProductId(),
    request.getSourceWarehouseId(),
    request.getDestinationWarehouseId(),
    request.getQuantity()
);

        Transfer transfer = new Transfer();
        transfer.setProductId(request.getProductId());
        transfer.setSourceWarehouseId(request.getSourceWarehouseId());
        transfer.setDestinationWarehouseId(request.getDestinationWarehouseId());
        transfer.setQuantity(request.getQuantity());
        transfer.setStatus("COMPLETED");
        transfer.setCreatedAt(LocalDateTime.now());

        return transferRepository.save(transfer);
    }
}