package com.twinkle.shopapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "detail_input_order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailInputOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "input_order_id")
    private InputOrder inputOrder;

    @Column(name = "price")
    private float price;

    @Column(name = "size")
    private Float size; // giá trên 1 sp

    @Column(name = "quantity")
    private int quantity;



}
