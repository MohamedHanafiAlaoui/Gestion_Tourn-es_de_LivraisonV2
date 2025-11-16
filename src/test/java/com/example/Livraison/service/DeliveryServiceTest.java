package com.example.Livraison.service;

import com.example.Livraison.dao.Repository.CustomerRepository;
import com.example.Livraison.dao.Repository.DeliveryHistoryRepository;
import com.example.Livraison.dao.Repository.DeliveryRepository;
import com.example.Livraison.dto.DeliveryDTO;
import com.example.Livraison.model.Customer;
import com.example.Livraison.model.Delivery;
import com.example.Livraison.model.enums.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private DeliveryHistoryRepository deliveryHistoryRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    @Test
    void create_shouldSetGpsFromCustomer_andStatusPending_andSave() {
        Long customerId = 42L;
        DeliveryDTO dto = new DeliveryDTO();
        dto.setCustomerId(customerId);
        dto.setAdresse("ADDR");
        dto.setPoidsKg(10.0);
        dto.setVolumeM3(1.0);

        Customer customer = Customer.builder()
                .id(customerId)
                .latitude(33.123456)
                .longitude(-7.654321)
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> {
            Delivery d = invocation.getArgument(0);
            d.setId(100L);
            return d;
        });

        DeliveryDTO result = deliveryService.create(dto);

        ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository, times(1)).save(captor.capture());
        Delivery saved = captor.getValue();

        assertThat(saved.getStatus()).isEqualTo(Status.PENDING);
        assertThat(saved.getGpsLat()).isEqualTo(33.123456);
        assertThat(saved.getGpsLon()).isEqualTo(-7.654321);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
    }

    @Test
    void update_whenDelivered_shouldSaveDeliveryHistory() {
        long id = 5L;
        Delivery existing = new Delivery();
        existing.setId(id);
        existing.setStatus(Status.PENDING);
        existing.setCustomer(Customer.builder().id(77L).build());
        existing.setAdresse("ADDR");
        existing.setGpsLat(1.0);
        existing.setGpsLon(2.0);

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DeliveryDTO dto = new DeliveryDTO();
        dto.setStatus(Status.DELIVERED);
        dto.setAdresse("ADDR2");
        dto.setGpsLat(3.0);
        dto.setGpsLon(4.0);
        dto.setPoidsKg(5.0);
        dto.setVolumeM3(0.5);
        dto.setCustomerId(77L);

        DeliveryDTO out = deliveryService.update(id, dto);

        verify(deliveryHistoryRepository, times(1)).save(any());

        ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository, times(1)).save(captor.capture());
        Delivery updated = captor.getValue();
        assertThat(updated.getStatus()).isEqualTo(Status.DELIVERED);
        assertThat(updated.getAdresse()).isEqualTo("ADDR2");
        assertThat(updated.getGpsLat()).isEqualTo(3.0);
        assertThat(updated.getGpsLon()).isEqualTo(4.0);
        assertThat(out).isNotNull();
    }
}