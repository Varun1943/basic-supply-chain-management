package com.app.supply_chain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.supply_chain.order.model.Order;
import java.util.Optional;
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdempotencyKey(String key);
}