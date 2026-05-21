package com.app.supply_chain.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.supply_chain.warehouse.model.WareHouse;

public interface WareHouseRepository extends JpaRepository<WareHouse, Long> {

}