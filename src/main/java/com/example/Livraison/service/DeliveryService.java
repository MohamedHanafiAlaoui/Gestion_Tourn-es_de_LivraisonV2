package com.example.Livraison.service;

import com.example.Livraison.dao.Repository.CustomerRepository;
import com.example.Livraison.dao.Repository.DeliveryHistoryRepository;
import com.example.Livraison.dao.Repository.DeliveryRepository;
import com.example.Livraison.dto.DeliveryDTO;
import com.example.Livraison.mapper.DeliveryMapper;
import com.example.Livraison.model.Customer;
import com.example.Livraison.model.Delivery;
import com.example.Livraison.model.DeliveryHistory;
import com.example.Livraison.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final CustomerRepository customerRepository;
    private final DeliveryHistoryRepository deliveryHistoryRepository;


    @Autowired
    public   DeliveryService(DeliveryRepository    deliveryRepository ,CustomerRepository customerRepository,DeliveryHistoryRepository deliveryHistoryRepository)
    {
        this.deliveryRepository = deliveryRepository;
        this.customerRepository = customerRepository;
        this.deliveryHistoryRepository = deliveryHistoryRepository;
    }

    public List<DeliveryDTO> findAll()
    {
        return deliveryRepository.findAll()
                .stream()
                .map(DeliveryDTO::toDto)
                .collect(Collectors.toList());
    }

    public  DeliveryDTO findById(long id)
    {
        Delivery  delivery  = deliveryRepository.findById(id)
                .orElseThrow(()-> new IllegalStateException("delivery with id " + id + " not found"));

        return DeliveryMapper.toDto(delivery);
    }

    public  DeliveryDTO create(DeliveryDTO dto)
    {
        Long customerId = dto.getCustomerId();
        Delivery entity = DeliveryMapper.toEntity(dto);
        entity.setStatus(Status.PENDING);
                Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer with id " + customerId + " not found"));

        entity.setGpsLat(customer.getLatitude());
        entity.setGpsLon(customer.getLongitude());
        

        Delivery delivery = deliveryRepository.save(entity);

        return DeliveryMapper.toDto(delivery);
    }

    public DeliveryDTO update(long id, DeliveryDTO dto) {
        Delivery existing = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Delivery with id " + id + " not found"));

        existing.setStatus(dto.getStatus());
        existing.setAdresse(dto.getAdresse());
        existing.setGpsLat(dto.getGpsLat());
        existing.setGpsLon(dto.getGpsLon());
        existing.setPoidsKg(dto.getPoidsKg());
        existing.setVolumeM3(dto.getVolumeM3());
        existing.setCreneauPref(dto.getCreneauPref());
        existing.setCustomer(dto.getCustomerId() != null ? Customer.builder().id(dto.getCustomerId()).build() : null);

        Delivery updatedDelivery = deliveryRepository.save(existing);

        if (dto.getStatus() == Status.DELIVERED) {
            Date actualTime = new Date();
            Date plannedTime = updatedDelivery.getTour() != null ? updatedDelivery.getTour().getDate() : null;
            Long delay = null;

            if (plannedTime != null) {
                delay = actualTime.getTime() - plannedTime.getTime(); // بالميلي ثانية
            }

            DeliveryHistory history = DeliveryHistory.builder()
                    .delivery(updatedDelivery)
                    .deliveryDate(new Date())
                    .plannedTime(plannedTime)
                    .actualTime(actualTime)
                    .delay(delay)
                    .dayOfWeek(new java.text.SimpleDateFormat("EEEE").format(actualTime))
                    .tour(updatedDelivery.getTour())
                    .customer(updatedDelivery.getCustomer())
                    .build();

            deliveryHistoryRepository.save(history);
        }

        return DeliveryMapper.toDto(updatedDelivery);
    }


    public  void delete(long id)
    {
        if (!deliveryRepository.existsById(id)) {
            throw new IllegalStateException("Delivery not found with ID: " + id);
        }
        deliveryRepository.deleteById(id);
    }





}
