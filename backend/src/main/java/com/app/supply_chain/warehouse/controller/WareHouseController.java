package com.app.supply_chain.warehouse.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.app.supply_chain.warehouse.model.WareHouse;
import com.app.supply_chain.warehouse.service.WareHouseService;

@RestController
@RequestMapping("/warehouses")
public class WareHouseController {

    private final WareHouseService wareHouseService;

    public WareHouseController(WareHouseService wareHouseService) {
        this.wareHouseService = wareHouseService;
    }

    @PostMapping
    public WareHouse createWarehouse(@RequestBody WareHouse warehouse) {
        return wareHouseService.createWarehouse(warehouse);
    }

    @GetMapping
    public List<WareHouse> getAllWarehouses() {
        return wareHouseService.getAllWarehouses();
    }
}