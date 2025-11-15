package com.example.Livraison.service;

import com.example.Livraison.dao.Repository.CustomerRepository;
import com.example.Livraison.dto.CustomerDTO;
import com.example.Livraison.mapper.CustomerMapper;
import com.example.Livraison.model.Customer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDTO> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toDto)
                .collect(Collectors.toList());
    }

    public CustomerDTO findById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with id " + id + " not found"));
        return CustomerMapper.toDto(customer);
    }

    public CustomerDTO create(CustomerDTO dto) {
        Customer entity = CustomerMapper.toEntity(dto);
        Customer saved = customerRepository.save(entity);
        return CustomerMapper.toDto(saved);
    }

    public CustomerDTO update(Long id, CustomerDTO dto) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with id " + id + " not found"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setLatitude(dto.getLatitude());
        existing.setLongitude(dto.getLongitude());
        existing.setPreferredTimeSlot(dto.getPreferredTimeSlot());

        Customer updated = customerRepository.save(existing);
        return CustomerMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer with id " + id + " not found");
        }
        customerRepository.deleteById(id);
    }
}
