package com.app.supply_chain.inventory.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.app.supply_chain.inventory.model.Inventory;
import com.app.supply_chain.inventory.service.InventoryService;
import com.app.supply_chain.inventory.dto.InventoryRequest;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/add")
    public Inventory addInventory(@RequestBody InventoryRequest request) {
        return inventoryService.addInventory(request);
    }

    @GetMapping("/all")
    public List<Inventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }
}