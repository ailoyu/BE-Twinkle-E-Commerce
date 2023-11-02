package com.twinkle.shopapp.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "product_price")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "price")
    private float price;

    @Column(name = "applied_date")
    private Date appliedDate;

}
