package com.example.Livraison.dao.Repository;

import com.example.Livraison.model.DeliveryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, Long> {
    List<DeliveryHistory> findByDelivery_Id(Long deliveryId);
    List<DeliveryHistory> findByTour_Id(Long tourId);
    List<DeliveryHistory> findByCustomer_Id(Long customerId);
    List<DeliveryHistory> findByDeliveryDateBetween(Date startDate, Date endDate);
}
