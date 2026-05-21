package com.app.supply_chain.warehouse.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.supply_chain.warehouse.model.WareHouse;
import com.app.supply_chain.warehouse.repository.WareHouseRepository;

@Service
public class WareHouseService {

    private final WareHouseRepository wareHouseRepository;

    public WareHouseService(WareHouseRepository wareHouseRepository) {
        this.wareHouseRepository = wareHouseRepository;
    }

    public WareHouse createWarehouse(WareHouse warehouse) {
        return wareHouseRepository.save(warehouse);
    }

    public List<WareHouse> getAllWarehouses() {
        return wareHouseRepository.findAll();
    }
}