package com.example.Livraison.mapper;

import com.example.Livraison.dto.CustomerDTO;
import com.example.Livraison.model.Customer;

public class CustomerMapper {
    
    private CustomerMapper() {}

    public static CustomerDTO toDto(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("Customer is null");
        return CustomerDTO.toDto(customer);
    }

    public static Customer toEntity(CustomerDTO dto) {
        if (dto == null) throw new IllegalArgumentException("CustomerDTO is null");
        return Customer.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .preferredTimeSlot(dto.getPreferredTimeSlot())
                .build();
    }
}
