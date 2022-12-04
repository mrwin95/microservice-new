package com.kenzoo.inventory.controller;

import com.kenzoo.inventory.dto.InventoryResponse;
import com.kenzoo.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@Slf4j
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
//    @PostMapping
//    @ResponseStatus(HttpStatus.OK)
//    public String createInventory(@RequestBody InventoryRequest inventoryRequest){
//
//    }

    @GetMapping
    public List<InventoryResponse> isInStock(@RequestParam("skuCode") List<String> skuCode){

        return inventoryService.isInStock(skuCode);
    }
}
