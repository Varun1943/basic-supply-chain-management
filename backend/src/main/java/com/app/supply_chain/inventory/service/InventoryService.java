package com.app.supply_chain.inventory.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.supply_chain.inventory.dto.InventoryRequest;
import com.app.supply_chain.inventory.model.Inventory;
import com.app.supply_chain.inventory.repository.InventoryRepository;
import com.app.supply_chain.inventory.reservation.model.InventoryReservation;
import com.app.supply_chain.inventory.reservation.model.ReservationStatus;
import com.app.supply_chain.inventory.reservation.repository.InventoryReservationRepository;
import com.app.supply_chain.product.model.Product;
import com.app.supply_chain.product.repository.ProductRepository;
import com.app.supply_chain.warehouse.model.WareHouse;
import com.app.supply_chain.warehouse.repository.WareHouseRepository;

import jakarta.annotation.PostConstruct;
// import ;
@Service

public class InventoryService {
    private final StringRedisTemplate redisTemplate;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WareHouseRepository warehouseRepository;
    // private final ExecutorService executor = Executors.newFixedThreadPool(50);
private final InventoryReservationRepository reservationRepository;
private final DefaultRedisScript<Long> reserveScript;
private final BlockingQueue<InventoryReservation> queue =
    new LinkedBlockingQueue<>(10000);
    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository,
            WareHouseRepository warehouseRepository,
            InventoryReservationRepository reservationRepository,
            StringRedisTemplate redisTemplate
            ) {

        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.reservationRepository = reservationRepository;
        this.redisTemplate = redisTemplate;
        this.reserveScript = new DefaultRedisScript<>();
this.reserveScript.setScriptText(
    "local current = redis.call('GET', KEYS[1]) " +
    "if not current then return -1 end " +
    "if tonumber(current) < tonumber(ARGV[1]) then return -1 end " +
    "return redis.call('DECRBY', KEYS[1], ARGV[1])"
);
this.reserveScript.setResultType(Long.class);
    }
    @PostConstruct
    public void preloadRedis() {
// System.out.println("PRELOAD STARTED");
    List<Inventory> all = inventoryRepository.findAll();
// System.out.println("Inventory count: " + all.size());
    for (Inventory inv : all) {

        String key = "stock:" + inv.getProduct().getId() + ":" + inv.getWarehouse().getId();

        redisTemplate.opsForValue().set(
            key,
            String.valueOf(inv.getAvailableQuantity())
        );
        // System.out.println("Loaded key: " + key);
    }

    // System.out.println("Redis preload complete");
}
    public Inventory addInventory(InventoryRequest request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow();

        WareHouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow();

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
       inventory.setAvailableQuantity(request.getQuantity());
        inventory.setReservedQuantity(0);

        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }
    
    public void reserveStock(Long productId, Long warehouseId, int qty) {

        // long start = System.currentTimeMillis();

        String key = "stock:" + productId + ":" + warehouseId;

        // long redisStart = System.currentTimeMillis();
        Long remaining = redisTemplate.execute(
    reserveScript,
    Collections.singletonList(key),
    String.valueOf(qty)
);
        // long redisTime = System.currentTimeMillis() - redisStart;

        if (remaining == null || remaining < 0) {
            // redisTemplate.opsForValue().increment(key, qty);
            throw new RuntimeException("Out of stock");
        }

    //     inventoryRepository.reserveStockAtomic(
    // productId,
    // warehouseId,
    // qty
// );
        // long dbStart = System.currentTimeMillis()/;

        // KEEP THIS TEMPORARILY to measure
        InventoryReservation reservation = new InventoryReservation();
        reservation.setProductId(productId);
        reservation.setWarehouseId(warehouseId);
        reservation.setQuantity(qty);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setOrderId(-1L);
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        queue.offer(reservation);

        // long dbTime = System.currentTimeMillis() - dbStart;

        // long total = System.currentTimeMillis() - start;

        // System.out.println(
        //     "DEBUG_TIMING | Redis: " + redisTime + "ms | DB: " + dbTime + "ms | TOTAL: " + total + "ms"
        // );
    }
    @Transactional
    public void releaseStock(Long productId, Long warehouseId, int qty, Long orderId) {

        InventoryReservation reservation = reservationRepository
                .findByOrderId(orderId)
                .orElseThrow();

        if (!reservation.getStatus().equals(ReservationStatus.ACTIVE)) {
            return;
        }

        // restore in Redis
        String key = "stock:" + productId + ":" + warehouseId;
        redisTemplate.opsForValue().increment(key, qty);

        reservation.setStatus(ReservationStatus.CANCELLED);
        queue.offer(reservation);
    }

@PostConstruct
public void startBatchWriter() {
   Executors.newSingleThreadExecutor().submit(() -> {
        while (true) {
            try {
                List<InventoryReservation> batch = new ArrayList<>();
                InventoryReservation first = queue.take();
                batch.add(first);
                Thread.sleep(10);
                queue.drainTo(batch, 999);

                    reservationRepository.saveAll(batch);
                // reservationRepository.flush();
// System.out.println("BATCH SIZE = " + batch.size());

                // Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
}

@Transactional
public void confirmStock(Long productId, Long warehouseId, int qty, Long orderId) {

    InventoryReservation reservation = reservationRepository
        .findByOrderId(orderId)
        .orElseThrow();

    if (!reservation.getStatus().equals(ReservationStatus.ACTIVE)) {
        return;
    }

    // (already deducted in Redis)
    reservation.setStatus(ReservationStatus.CONFIRMED);

    queue.offer(reservation);
}
@Transactional
public void transferStock(Long productId,
                          Long sourceWarehouseId,
                          Long destinationWarehouseId,
                          int qty) {

    // lock source
    Inventory source = inventoryRepository
        .findByProductAndWarehouseForUpdate(productId, sourceWarehouseId)
        .orElseThrow(() -> new RuntimeException("Source inventory not found"));

    if (source.getAvailableQuantity() < qty) {
        throw new RuntimeException("Not enough stock");
    }

    //  lock destination
    Inventory destination = inventoryRepository
        .findByProductAndWarehouseForUpdate(productId, destinationWarehouseId)
        .orElse(null);

    //  create if not exists
    if (destination == null) {

        destination = new Inventory();

        destination.setProduct(
            productRepository.findById(productId).orElseThrow()
        );

        destination.setWarehouse(
            warehouseRepository.findById(destinationWarehouseId).orElseThrow()
        );

        destination.setAvailableQuantity(0);
        destination.setReservedQuantity(0);
    }

    // update values
    source.setAvailableQuantity(source.getAvailableQuantity() - qty);
    destination.setAvailableQuantity(destination.getAvailableQuantity() + qty);

    inventoryRepository.save(source);
    inventoryRepository.save(destination);
}
}