package com.twinkle.shopapp.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "provider")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "address", length = 250)
    private String address;

    @Column(name = "website")
    private String website;

    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Column(name = "email", length = 100) // maximum chữ là 350 chữ
    private String email;

    @Column(name = "description")
    private String description;

    @JsonManagedReference
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<InputOrder> inputOrders = new ArrayList<>();



}
