package com.example.Livraison.service;

import com.example.Livraison.dao.Repository.DeliveryHistoryRepository;
import com.example.Livraison.dto.DeliveryHistoryDTO;
import com.example.Livraison.mapper.DeliveryHistoryMapper;
import com.example.Livraison.model.DeliveryHistory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class DeliveryHistoryService {

    private final DeliveryHistoryRepository deliveryHistoryRepository;

    public DeliveryHistoryService(DeliveryHistoryRepository deliveryHistoryRepository) {
        this.deliveryHistoryRepository = deliveryHistoryRepository;
    }

    public List<DeliveryHistoryDTO> getAll() {
        return deliveryHistoryRepository.findAll()
                .stream()
                .map(DeliveryHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public DeliveryHistoryDTO getById(Long id) {
        Optional<DeliveryHistory> deliveryHistoryOpt = deliveryHistoryRepository.findById(id);
        return deliveryHistoryOpt.map(DeliveryHistoryMapper::toDto).orElse(null);
    }

    public DeliveryHistoryDTO create(DeliveryHistoryDTO dto) {
        DeliveryHistory entity = DeliveryHistoryMapper.toEntity(dto);
        DeliveryHistory saved = deliveryHistoryRepository.save(entity);
        return DeliveryHistoryMapper.toDto(saved);
    }

    public DeliveryHistoryDTO update(Long id, DeliveryHistoryDTO dto) {
        return deliveryHistoryRepository.findById(id)
                .map(existing -> {
                    existing.setDeliveryDate(dto.getDeliveryDate());
                    existing.setPlannedTime(dto.getPlannedTime());
                    existing.setActualTime(dto.getActualTime());
                    existing.setDayOfWeek(dto.getDayOfWeek());

                    DeliveryHistory updated = deliveryHistoryRepository.save(existing);
                    return DeliveryHistoryMapper.toDto(updated);
                })
                .orElse(null);
    }

    public void delete(Long id) {
        deliveryHistoryRepository.deleteById(id);
    }

    public List<DeliveryHistoryDTO> getByDeliveryId(Long deliveryId) {
        List<DeliveryHistory> list = deliveryHistoryRepository.findByDelivery_Id(deliveryId);
        return list.stream().map(DeliveryHistoryMapper::toDto).collect(Collectors.toList());
    }

    public List<DeliveryHistoryDTO> getByTourId(Long tourId) {
        List<DeliveryHistory> list = deliveryHistoryRepository.findByTour_Id(tourId);
        return list.stream().map(DeliveryHistoryMapper::toDto).collect(Collectors.toList());
    }
}
