package com.example.Livraison.mapper;

import com.example.Livraison.dto.DeliveryHistoryDTO;
import com.example.Livraison.model.Delivery;
import com.example.Livraison.model.DeliveryHistory;
import com.example.Livraison.model.Tour;

public class DeliveryHistoryMapper {

    private DeliveryHistoryMapper() {
    }

    public static DeliveryHistoryDTO toDto(DeliveryHistory deliveryHistory) {
        if (deliveryHistory == null) return null;

        return DeliveryHistoryDTO.builder()
                .id(deliveryHistory.getId())
                .deliveryDate(deliveryHistory.getDeliveryDate())
                .plannedTime(deliveryHistory.getPlannedTime())
                .actualTime(deliveryHistory.getActualTime())
                .dayOfWeek(deliveryHistory.getDayOfWeek())
                .deliveryId(deliveryHistory.getDelivery() != null
                        ? deliveryHistory.getDelivery().getId()
                        : null)
                .tourId(deliveryHistory.getTour() != null
                        ? deliveryHistory.getTour().getId()
                        : null)
                .build();
    }

    public static DeliveryHistory toEntity(DeliveryHistoryDTO dto) {
        if (dto == null) return null;

        DeliveryHistory.DeliveryHistoryBuilder builder = DeliveryHistory.builder()
                .id(dto.getId())
                .deliveryDate(dto.getDeliveryDate())
                .plannedTime(dto.getPlannedTime())
                .actualTime(dto.getActualTime())
                .dayOfWeek(dto.getDayOfWeek());

        if (dto.getDeliveryId() != null) {
            builder.delivery(Delivery.builder().id(dto.getDeliveryId()).build());
        }

        if (dto.getTourId() != null) {
            builder.tour(Tour.builder().id(dto.getTourId()).build());
        }

        return builder.build();
    }
}
