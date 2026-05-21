package com.app.supply_chain.WarehouseTransfer.controller;
import org.springframework.web.bind.annotation.*;
import com.app.supply_chain.WarehouseTransfer.model.Transfer;
import com.app.supply_chain.WarehouseTransfer.service.TransferService;
import com.app.supply_chain.WarehouseTransfer.dto.TransferRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public Transfer transfer(@RequestBody TransferRequest request) {
        return transferService.transferStock(request);
    }
}