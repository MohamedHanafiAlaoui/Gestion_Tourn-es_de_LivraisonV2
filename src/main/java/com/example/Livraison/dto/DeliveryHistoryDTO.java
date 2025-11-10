package com.example.Livraison.dto;

import com.example.Livraison.model.DeliveryHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryHistoryDTO {

    private Long id;
    private Date deliveryDate;
    private Date plannedTime;
    private Date actualTime;
    private String dayOfWeek;
    private Long deliveryId;
    private Long tourId;

    public static DeliveryHistoryDTO toDto(DeliveryHistory deliveryHistory) {
        if (deliveryHistory == null) return null;

        return DeliveryHistoryDTO.builder()
                .id(deliveryHistory.getId())
                .deliveryDate(deliveryHistory.getDeliveryDate())
                .plannedTime(deliveryHistory.getPlannedTime())
                .actualTime(deliveryHistory.getActualTime())
                .dayOfWeek(deliveryHistory.getDayOfWeek())
                .deliveryId(
                        deliveryHistory.getDelivery() != null
                                ? deliveryHistory.getDelivery().getId()
                                : null
                )
                .tourId(
                        deliveryHistory.getTour() != null
                                ? deliveryHistory.getTour().getId()
                                : null
                )
                .build();
    }
}
