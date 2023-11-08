package com.twinkle.shopapp.responses;

import lombok.*;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
public class OrderRow {
    private String order_date;
    private long order_id;
    private String customer_name;
    private String customer_phone;
    private float gia_goc;
    private float gia_loi_nhuan;
    private int number_of_products;
    private float tong_gia_loi_nhuan;
    private double loi_nhuan;

}
