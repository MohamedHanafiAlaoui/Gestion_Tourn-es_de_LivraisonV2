package com.example.Livraison.service;

import com.example.Livraison.dao.Repository.CustomerRepository;
import com.example.Livraison.dao.Repository.DeliveryHistoryRepository;
import com.example.Livraison.dao.Repository.DeliveryRepository;
import com.example.Livraison.dao.Repository.TourRepository;
import com.example.Livraison.dao.Repository.VehiculeRepository;
import com.example.Livraison.dao.Repository.WarehouseRepository;
import com.example.Livraison.model.Customer;
import com.example.Livraison.model.Delivery;
import com.example.Livraison.model.DeliveryHistory;
import com.example.Livraison.model.Tour;
import com.example.Livraison.model.Vehicule;
import com.example.Livraison.model.Warehouse;
import com.example.Livraison.model.enums.EtatVehicule;
import com.example.Livraison.model.enums.Status;
import com.example.Livraison.model.enums.TypeVehicule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Profile("qa")
public class TestDataInitializer implements CommandLineRunner {

    private final WarehouseRepository warehouseRepository;
    private final VehiculeRepository vehiculeRepository;
    private final CustomerRepository customerRepository;
    private final DeliveryRepository deliveryRepository;
    private final TourRepository tourRepository;
    private final DeliveryHistoryRepository deliveryHistoryRepository;

    public TestDataInitializer(WarehouseRepository warehouseRepository,
                               VehiculeRepository vehiculeRepository,
                               CustomerRepository customerRepository,
                               DeliveryRepository deliveryRepository,
                               TourRepository tourRepository,
                               DeliveryHistoryRepository deliveryHistoryRepository) {
        this.warehouseRepository = warehouseRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.customerRepository = customerRepository;
        this.deliveryRepository = deliveryRepository;
        this.tourRepository = tourRepository;
        this.deliveryHistoryRepository = deliveryHistoryRepository;
    }

    @Override
    public void run(String... args) {
        if (warehouseRepository.count() > 0 && customerRepository.count() > 0 && deliveryRepository.count() > 0) {
            return; // already seeded
        }

        Random rnd = new Random(42);

        // 1) Warehouse (Casablanca)
        Warehouse wh = Warehouse.builder()
                .adresse("Bd. Mohammed V, Casablanca")
                .gpsLat(33.5731104)
                .gpsLong(-7.5898434)
                .horaireOuverture("08:00")
                .horaireFermeture("18:00")
                .build();
        wh = warehouseRepository.save(wh);

        // 2) Vehicules (3)
        List<Vehicule> vehicules = new ArrayList<>();
        vehicules.add(Vehicule.builder()
                .type(TypeVehicule.CAMION)
                .capaciteMaxKg(1500)
                .capaciteMaxM3(15)
                .etat(EtatVehicule.DISPONIBLE)
                .dateAjout(new Date())
                .build());
        vehicules.add(Vehicule.builder()
                .type(TypeVehicule.FOURGON)
                .capaciteMaxKg(800)
                .capaciteMaxM3(8)
                .etat(EtatVehicule.DISPONIBLE)
                .dateAjout(new Date())
                .build());
        vehicules.add(Vehicule.builder()
                .type(TypeVehicule.MOTO)
                .capaciteMaxKg(80)
                .capaciteMaxM3(0.6)
                .etat(EtatVehicule.DISPONIBLE)
                .dateAjout(new Date())
                .build());
        vehicules = vehiculeRepository.saveAll(vehicules);

        // 3) Customers (20) around Casablanca
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            double lat = 33.55 + rnd.nextDouble() * 0.08; // 33.55..33.63
            double lon = -7.64 + rnd.nextDouble() * 0.12; // -7.64..-7.52
            customers.add(Customer.builder()
                    .firstName("Customer")
                    .lastName(String.valueOf(i))
                    .latitude(lat)
                    .longitude(lon)
                    .build());
        }
        customers = customerRepository.saveAll(customers);

        // 4) Deliveries (40) linked to customers
        List<Delivery> deliveries = new ArrayList<>();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        for (int i = 1; i <= 40; i++) {
            Customer c = customers.get(rnd.nextInt(customers.size()));
            cal.setTime(now);
            cal.add(Calendar.HOUR, rnd.nextInt(72) - 24); // +/- 24h within 3 days
            Date planned = cal.getTime();

            deliveries.add(Delivery.builder()
                    .adresse("CAS-" + (c.getId() != null ? c.getId() : i))
                    .gpsLat(c.getLatitude())
                    .gpsLon(c.getLongitude())
                    .poidsKg(1 + rnd.nextInt(30))
                    .volumeM3(Math.round((0.1 + rnd.nextDouble() * 1.5) * 100.0) / 100.0)
                    .creneauPref(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(planned))
                    .status(Status.PENDING)
                    .customer(c)
                    .build());
        }
        deliveries = deliveryRepository.saveAll(deliveries);

        // 5) Tour today with FOURGON and ~12 deliveries
        Vehicule tourVeh = vehicules.get(1); // FOURGON
        Tour tour1 = Tour.builder()
                .date(new Date())
                .vehicule(tourVeh)
                .warehouse(wh)
                .build();
        tour1 = tourRepository.save(tour1);

        List<Delivery> toAssign = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Delivery d = deliveries.get(i);
            d.setTour(tour1);
            d.setStatus(Status.ASSIGNED);
            toAssign.add(d);
        }
        deliveryRepository.saveAll(toAssign);

        // 6) DeliveryHistory for 10 deliveries (yesterday)
        List<DeliveryHistory> histories = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Delivery d = deliveries.get(20 + i);
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Date planned = cal.getTime();
            cal.add(Calendar.MINUTE, 10 + rnd.nextInt(50));
            Date actual = cal.getTime();
            Long delay = actual.getTime() - planned.getTime();

            histories.add(DeliveryHistory.builder()
                    .delivery(d)
                    .deliveryDate(actual)
                    .plannedTime(planned)
                    .actualTime(actual)
                    .delay(delay)
                    .dayOfWeek(new java.text.SimpleDateFormat("EEEE").format(actual))
                    .tour(tour1)
                    .customer(d.getCustomer())
                    .build());
        }
        deliveryHistoryRepository.saveAll(histories);

        // 7) Second tour tomorrow with CAMION and 10 deliveries
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Tour tour2 = Tour.builder()
                .date(cal.getTime())
                .vehicule(vehicules.get(0)) // CAMION
                .warehouse(wh)
                .build();
        final Tour savedTour2 = tourRepository.save(tour2);

        List<Delivery> toAssign2 = deliveries.stream()
                .skip(12)
                .limit(10)
                .peek(d -> {
                    d.setTour(savedTour2);
                    d.setStatus(Status.ASSIGNED);
                })
                .collect(Collectors.toList());
        deliveryRepository.saveAll(toAssign2);
    }
}