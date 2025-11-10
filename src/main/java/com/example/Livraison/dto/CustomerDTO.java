package com.example.Livraison.dto;

import com.example.Livraison.model.Customer;
import com.example.Livraison.model.Delivery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDTO {
    
    private Long id;
    private String firstName;
    private String lastName;
    private double latitude;
    private double longitude;
    private String preferredTimeSlot;
    private List<Long> deliveryIds;

    public static CustomerDTO toDto(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("Customer is null");
        return CustomerDTO.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .latitude(customer.getLatitude())
                .longitude(customer.getLongitude())
                .preferredTimeSlot(customer.getPreferredTimeSlot())
                .deliveryIds(customer.getDeliveries() != null
                        ? customer.getDeliveries().stream()
                        .map(Delivery::getId)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }
}
