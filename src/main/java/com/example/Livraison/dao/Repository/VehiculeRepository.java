package com.example.Livraison.dao.Repository;

import com.example.Livraison.model.Vehicule;
import com.example.Livraison.model.enums.EtatVehicule;
import com.example.Livraison.model.enums.TypeVehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    List<Vehicule> findByOrderByCapaciteMaxKg();


    @Query(value = "SELECT * from vehicule  ORDER  BY capacite_max_kg DESC ", nativeQuery = true)
    List<Vehicule> findVehiculeByCapaciteMaxKg();

}
