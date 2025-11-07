package com.example.Livraison.service;

import com.example.Livraison.model.Delivery;
import com.example.Livraison.model.Vehicule;
import org.springframework.stereotype.Service;

import java.util.List;
@Service

public interface TourOptimizer {

    List<Delivery> calculateOptimalTour(List<Delivery> deliveries, Vehicule vehicule);
    double getTotalDistance(List<Delivery> deliveries);

}
