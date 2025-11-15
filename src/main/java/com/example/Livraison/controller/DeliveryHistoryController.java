package com.example.Livraison.controller;

import com.example.Livraison.dto.DeliveryHistoryDTO;
import com.example.Livraison.service.DeliveryHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/delivery-histories")
public class DeliveryHistoryController {

    private final DeliveryHistoryService deliveryHistoryService;

    public DeliveryHistoryController(DeliveryHistoryService deliveryHistoryService) {
        this.deliveryHistoryService = deliveryHistoryService;
    }

    @GetMapping
    public List<DeliveryHistoryDTO> getAllHistories() {
        return deliveryHistoryService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryHistoryDTO> getHistoryById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryHistoryService.getById(id));
    }

    @GetMapping("/delivery/{deliveryId}")
    public List<DeliveryHistoryDTO> getHistoriesByDelivery(@PathVariable Long deliveryId) {
        return deliveryHistoryService.getByDeliveryId(deliveryId);
    }





    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        deliveryHistoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
