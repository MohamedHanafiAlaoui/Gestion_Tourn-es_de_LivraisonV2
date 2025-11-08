package com.example.Livraison.model;
import com.example.Livraison.model.Delivery;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private double latitude;
    private double longitude;

    private String preferredTimeSlot;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Delivery> deliveries;
}
