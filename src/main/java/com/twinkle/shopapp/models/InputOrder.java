package com.twinkle.shopapp.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "input_order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InputOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @JsonManagedReference
    @OneToMany(mappedBy = "inputOrder", cascade = CascadeType.ALL)
    private List<DetailInputOrder> detailInputOrders = new ArrayList<>();


}
