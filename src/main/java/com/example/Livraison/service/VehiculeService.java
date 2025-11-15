package com.example.Livraison.service;

import com.example.Livraison.dao.Repository.VehiculeRepository;
import com.example.Livraison.dto.VehiculeDTO;
import com.example.Livraison.mapper.VehiculeMapper;
import com.example.Livraison.model.Vehicule;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    public List<VehiculeDTO> findAll() {
        return vehiculeRepository.findAll()
                .stream()
                .map(VehiculeMapper::toDto)
                .collect(Collectors.toList());
    }

    public VehiculeDTO findById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Vehicule with id " + id + " not found"));
        return VehiculeMapper.toDto(vehicule);
    }

    public VehiculeDTO create(VehiculeDTO dto) {
        Vehicule entity = VehiculeMapper.toEntity(dto);
        Vehicule saved = vehiculeRepository.save(entity);
        return VehiculeMapper.toDto(saved);
    }

    public VehiculeDTO update(Long id, VehiculeDTO dto) {
        Vehicule existing = vehiculeRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Vehicule not found with ID: " + id));
        existing.setType(dto.getType());
        existing.setEtat(dto.getEtat());
        existing.setCapaciteMaxKg(dto.getCapaciteMaxKg());
        existing.setCapaciteMaxM3(dto.getCapaciteMaxM3());
        Vehicule updated = vehiculeRepository.save(existing);
        return VehiculeMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new IllegalStateException("Vehicule not found with ID: " + id);
        }
        vehiculeRepository.deleteById(id);
    }

    public List<VehiculeDTO> findByTypeSortWeight() {
        List<Vehicule> vehicules = vehiculeRepository.findVehiculeByCapaciteMaxKg();
        return vehicules.stream()
                .map(VehiculeMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<VehiculeDTO> findByType() {
        List<Vehicule> vehicules = vehiculeRepository.findByOrderByCapaciteMaxKg();
        return vehicules.stream()
                .map(VehiculeMapper::toDto)
                .collect(Collectors.toList());
    }
}
