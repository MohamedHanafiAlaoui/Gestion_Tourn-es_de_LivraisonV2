package com.example.Livraison.mapper;

import com.example.Livraison.dto.VehiculeDTO;
import com.example.Livraison.model.Vehicule;

public class VehiculeMapper {
    private VehiculeMapper() {}

    public static VehiculeDTO toDto(Vehicule vehicule) {
        if (vehicule == null) throw new IllegalArgumentException("Vehicule cannot be null");
        return VehiculeDTO.builder()
                .id(vehicule.getId())
                .type(vehicule.getType())
                .capaciteMaxKg(vehicule.getCapaciteMaxKg())
                .capaciteMaxM3(vehicule.getCapaciteMaxM3())
                .etat(vehicule.getEtat())
                .dateAjout(vehicule.getDateAjout())
                .build();
    }

    public static Vehicule toEntity(VehiculeDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO cannot be null");
        return Vehicule.builder()
                .id(dto.getId())
                .type(dto.getType())
                .capaciteMaxKg(dto.getCapaciteMaxKg())
                .capaciteMaxM3(dto.getCapaciteMaxM3())
                .etat(dto.getEtat())
                .dateAjout(dto.getDateAjout())
                .build();
    }
}
